# Repository Guidelines

## Project Structure & Module Organization

This is a Java 8 multi-module Maven project rooted at `pom.xml`. Source code follows the standard Maven layout: `src/main/java`, `src/main/resources`, `src/test/java`, and `src/test/resources`.

- `my-ddd-demo-api`: public command facade contracts and DTOs.
- `my-ddd-demo-app`: application services, factories, converters, and published events.
- `my-ddd-demo-domain`: domain entities, value objects, and repository interfaces.
- `myb-ddd-demo-infra`: infrastructure implementations, gateways, persistence DOs, and MQ utilities.
- `my-ddd-demo-adapter`: adapter implementations that bridge API contracts to application services.
- `my-ddd-demo-start`: startup/assembly module.

Keep package names under `com.viw.ddd.demo`. Preserve the DDD layering direction: API/adapter should call application services; application code coordinates domain and infrastructure; domain code should not depend on infrastructure.

## Build, Test, and Development Commands

- `mvn clean compile`: compile all modules from the repository root.
- `mvn clean test`: run the full Maven test lifecycle for every module.
- `mvn clean package`: build module artifacts after tests pass.
- `mvn -pl my-ddd-demo-app -am test`: test one module and any required upstream modules.

Run Maven commands from the root unless you are intentionally working inside a single module.

## Coding Style & Naming Conventions

Use Java 8-compatible code, UTF-8 files, and 4-space indentation. Follow existing class suffixes: `*Command`, `*DTO`, `*Entity`, `*VO`, `*Repository`, `*Gateway`, `*Service`, `*ServiceImpl`, and `*Convert`. Prefer small methods that keep orchestration in `app`, business state in `domain`, and external integration details in `infra`. Lombok is available in API/domain modules; use it consistently with nearby code.

## Testing Guidelines

Test directories exist, but no test framework dependency is currently configured. When adding tests, add the required Maven test dependency in the relevant module or parent POM. Place tests under the matching module's `src/test/java` tree and name them `*Test`, mirroring the production package. Focus coverage on domain behavior, application service orchestration, and repository/gateway implementations.

## Commit & Pull Request Guidelines

The repository currently has no commit history, so use concise imperative commit messages such as `Add apply order service validation` or `Fix express event publishing`. For pull requests, include a short purpose statement, affected modules, test results from Maven, and linked issue/task references when available. Add screenshots only for UI-facing changes.

## Security & Configuration Tips

Do not commit local IDE metadata, generated build output, credentials, or environment-specific configuration. Keep secrets outside source files and pass configuration through environment variables or deployment-specific files.

---

# Full Engineering Analysis Report (2026-05-15)

## 1. Module Overview

| Module | ArtifactId | Java Files | POM Dependencies | 关键类 |
|--------|-----------|-----------|-----------------|--------|
| api | my-ddd-demo-api | 3 | lombok | ApplyOrderCommandFacade (接口), SubmitApplyOrderCommand, SendExpressCommand (DTO) |
| domain | my-ddd-demo-domain | 4 | lombok | ApplyOrderEntity, ApplyOrderRepository (接口), ApplyOrderDetailVO, ApplyOrderExpressVO |
| app | my-ddd-demo-app | 6 | api, domain, infra, fastjson | ApplyOrderService (接口+Impl), ApplyOrderFactory, ApplyOrderConvert (接口), ApplyOrderSubmittedEvent, ExpressSentEvent |
| adapter | my-ddd-demo-adapter | 1 | api, app | ApplyOrderCommandFacadeImpl |
| infra | myb-ddd-demo-infra | 6 | domain | ApplyOrderRepositoryImpl, CompanyGateway (接口+Impl), CompanyDTO, MqSender (接口+Impl) |
| start | my-ddd-demo-start | 1 | (none) | Main.java (空骨架) |

## 2. Skeleton Analysis

### 已有初步实现的：
- **ApplyOrderServiceImpl** — submit/sendExpress 两个方法有流程骨架（查询→创建→保存→发MQ）
- **ApplyOrderFactory.clone()** — 用 fastjson 深拷贝（有实现）
- **ApplyOrderEntity.sendExpress()** — 有部分实现（调VO的sned+改状态）
- **ApplyOrderExpressVO.sned()** — 有部分实现（赋值expressNo）

### 纯骨架（只有签名/return默认值）：
- **ApplyOrderCommandFacadeImpl** — 空类，没实现任何方法
- **ApplyOrderEntity.create()/approve()/createBatch()/finishInvoice()/sendMail()** — 5个方法空实现
- **ApplyOrderFactory.createApplyOrder()** — 返回 builder.build()，所有字段 null
- **ApplyOrderConvert** — 接口，无实现类
- **ApplyOrderRepositoryImpl.save()** — 返回 0L，无持久化逻辑
- **ApplyOrderRepositoryImpl.findById()** — 返回 null
- **ApplyOrderRepositoryImpl.update()** — 返回 0，todo 注释未实现
- **CompanyGatewayImpl.findByCompanyId()** — 返回空的 builder.build()
- **MqSenderImpl.send()** — 空方法体

## 3. 代码问题

### ❌ 严重问题

**① Package 名称拼写错误：`domin` → `domain`** ✅ 已修复
- 9 个 Java 文件 + 文档已完成重命名

**② Module 命名不一致：`myb-ddd-demo-infra`**
- 其他模块：`my-ddd-demo-api/app/adapter/domain/start`
- infra 模块：`myb-ddd-demo-infra`（多了一个 b）
- 影响：pom.xml 中 `<module>` 和所有依赖声明都要改，工作量较大

**③ App 层直接依赖 Infra 层（违反 DDD 规范）**
- `app/pom.xml` 直接引入了 `myb-ddd-demo-infra` 依赖
- `ApplyOrderServiceImpl` 直接 import `CompanyGateway` 和 `MqSender`（都在 infra 包下）
- 正确做法：在 domain 层定义 Gateway 接口，infra 层实现，app 层只依赖 domain 接口
- `CompanyGateway` 接口应移到 domain 层

**④ 缺少 IoC/DI 框架**
- 所有字段无注入（无 `@Autowired`/构造函数注入）
- No Spring/Spring Boot dependency in any POM
- ApplyOrderServiceImpl 的 4 个依赖（repository/gateway/mq/convert）都是 null

**⑤ Infra DO 文件缺少 `.java` 后缀**
- `ApplyOrderDO`、`ApplyOrderDetailDO`、`ApplyOrderExpressDO` 在资源管理器中显示为无后缀文件
- 无法编译，需要重命名为 `.java` 或检查文件内容

**⑥ Convert 接口无实现**
- `ApplyOrderConvert` 定义为接口，但没有 `ApplyOrderConvertImpl`
- `ApplyOrderServiceImpl` 中调用了 `applyOrderConvert.convertApplyOrderSubmittedEvent()` 等，运行时 NPE

### ⚠️ 一般问题

- **`sned()` 拼写错误** → 应为 `send()` (ApplyOrderExpressVO.java 第27行)
- **Adapter 层未实现** — ApplyOrderCommandFacadeImpl 空类
- **Start 模块无依赖无配置** — 没有 application.yml，没有 SpringBoot 启动类
- **DO 文件路径不一致** — infra 下的 DO 文件在 `repository/do/` 包，但命名看起来像文件而不是 `.java`
- **Repository 接口设计** — `findById(Long id)` 和 `findById(Long id, String type)` 方法名相同但语义不同，可用重命名或 Strategy 模式

## 4. TODO 清单（按优先级）

### P0 — 阻塞编译的
- [x] 修复 DO 文件后缀（加 `.java`）
- [x] 补充 `ApplyOrderConvert` 实现类
- [x] 添加 Spring Boot 依赖和启动类
- [x] 修复 `domin` → `domain` 包名（9 个文件 + 文档）

### P1 — 核心业务逻辑
- [x] 实现 `ApplyOrderEntity` 的业务方法：create()/approve()/createBatch()/finishInvoice()/sendMail() — 含状态流转校验
- [x] 实现 `ApplyOrderFactory.createApplyOrder()` — 从 Command 组装完整 Entity
- [x] 实现 `ApplyOrderRepositoryImpl` 的持久化逻辑（内存Map + Entity↔DO转换）
- [ ] 实现 `CompanyGatewayImpl` — 调用远程 RPC 查询公司信息
- [ ] 实现 `MqSenderImpl.send()` — 实际发送 MQ 消息

### P2 — DDD 架构合规
- [ ] 将 `CompanyGateway` 接口从 infra 层移到 domain 层
- [ ] 移除 app 层对 infra 层的直接依赖，改为通过 domain 接口
- [ ] 添加 DI 框架（Spring 注解或构造函数注入）
- [ ] 修复 `myb-ddd-demo-infra` → `my-ddd-demo-infra`（可选，但建议统一下）

### P3 — 完善与测试
- [ ] 实现 `ApplyOrderCommandFacadeImpl` — 桥接 API → App 层
- [ ] 配置 `my-ddd-demo-start` 模块（application.yml、启动类、扫描路径）
- [ ] 添加 Maven 测试框架依赖（JUnit/Mockito）
- [ ] 修复 `sned()` 拼写 → `send()`

## 5. DDD 架构合规性评估

```
api          → 只定义接口契约 ✓
adapter      → 依赖 api + app ✓
app          → 依赖 api + domain + infra ✗（应该只依赖 api + domain）
domain       → 纯 POJO，无外部依赖 ✓ 但包名拼写错误
infra        → 依赖 domain ✓（Repository 接口定义在 domain）
start        → 无依赖（需要添加）
```

**当前 DDD 依赖方向**：
```
适配 → Controller/API → Application Service → Domain Entity/Repository
                       → Infra Gateway/MQ  ↑
                            （← 违反规范，app 不应直接依赖 infra）
```

**正确 DDD 依赖方向**：
```
适配 → Controller/API → Application Service → Domain Entity + Repository接口
                                                     ↓
                                               Infra 实现 Repository
                                               Infra 实现 Gateway
                                               Infra 发送 MQ
```

**关键整改**：将 `CompanyGateway` 接口定义迁移到 domain 层，`MqSender` 同理。这样 app 层就只需要 `domain`（含 Gateway+Repository 接口）和 `api`（含 Command DTO），不再依赖 `infra`。
