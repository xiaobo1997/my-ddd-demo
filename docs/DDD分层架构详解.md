# DDD 分层架构详解 —— 以 my-ddd-demo 为例

> 本文档配合仓库代码阅读，逐层讲解 DDD（领域驱动设计）的四层架构。
> 每一层都对应仓库中的实际代码，方便对照学习。

---

## 一、先看全貌：四层架构图

```
┌─────────────────────────────────────────────────────────┐
│                    接口层 (API)                           │
│  定义对外契约：Command、DTO、Facade 接口                    │
│  不包含任何业务逻辑                                        │
└───────────┬─────────────────────────────┬───────────────┘
            │                             │
    ┌───────▼────────┐           ┌───────▼────────┐
    │  适配层(Adapter)│           │  应用层(App)    │
    │  实现API接口    │──────────▶│  编排业务流程    │
    │  Controller等   │  调用      │  Service/Factory│
    └────────────────┘           └───┬───┬─────────┘
                                    │   │
                          ┌─────────▼┐  │
                          │领域层(Domain)│◀──────────┐
                          │Entity/VO     │           │
                          │Repository接口│           │
                          │Gateway接口   │           │
                          └──────┬───────┘           │
                                 │                    │
                          ┌──────▼───────────────────┴──┐
                          │    基础设施层 (Infra)        │
                          │  RepositoryImpl (DB)         │
                          │  GatewayImpl (RPC/HTTP)      │
                          │  MqSenderImpl (MQ)           │
                          └──────────────────────────────┘
```

**核心原则**：依赖方向从外向内，领域层是核心，不依赖任何外部。

---

## 二、逐层详解

### 1. 接口层 (API) — `my-ddd-demo-api`

**定位**：定义对外暴露的"契约"，告诉调用方"我能做什么、需要什么参数"。

**代码位置**：`my-ddd-demo-api/src/main/java/com/viw/ddd/demo/api/`

**关键文件**：
- `ApplyOrderCommandFacade.java` — 门面接口，定义"申请单"相关的所有操作
- `SubmitApplyOrderCommand.java` — 命令对象，"提交申请单"需要的参数
- `SendExpressCommand.java` — 命令对象，"发送快递"需要的参数

**DDD 知识点**：

| 概念 | 解释 | 对应代码 |
|------|------|----------|
| Command | 显式的"意图表达"，一个Command = 一次操作 | `SubmitApplyOrderCommand` |
| Facade | 门面模式，对外统一入口 | `ApplyOrderCommandFacade` |
| DTO | 数据传输对象，纯数据结构，无行为 | Command 中的字段 |

**面试要点**：
> 问：为什么用 Command 而不是 xxxRequest？
> 答：Command 是 CQRS（命令查询职责分离）的概念，"提交申请单"是一个命令而非查询。它携带了完成这个命令所需的全部参数，语义更清晰。

---

### 2. 适配层 (Adapter) — `my-ddd-demo-adapter`

**定位**：连接外部世界和内部世界的"翻译官"。实现 API 接口，把外部请求转成内部调用。

**代码位置**：`my-ddd-demo-adapter/src/main/java/com/viw/ddd/demo/adapter/`

**关键文件**：
- `ApplyOrderCommandFacadeImpl.java` — 实现 API 门面，桥接 Controller 到 App 层

**DDD 知识点**：

| 概念 | 解释 |
|------|------|
| Adapter | 适配器模式，隔离外部协议（HTTP/RPC）和内部模型 |
| 为什么需要这一层 | Controller 是技术细节（Spring MVC），不应该是领域的一部分 |

**面试要点**：
> 问：为什么 API 和 Adapter 要分开？
> 答：API 定义"做什么"（契约），Adapter 定义"怎么接"（适配）。API 可以给 Dubbo/HTTP/gRPC 共用，每种协议只需要不同的 Adapter 实现。

---

### 3. 应用层 (App) — `my-ddd-demo-app`

**定位**：业务流程的"导演"。它不包含业务逻辑，只负责编排。

**代码位置**：`my-ddd-demo-app/src/main/java/com/viw/ddd/demo/app/`

**关键文件**：
- `ApplyOrderServiceImpl.java` — 应用服务，编排"提交申请单"流程
- `ApplyOrderFactory.java` — 工厂，负责创建聚合根
- `ApplyOrderConvert.java` / `ApplyOrderConvertImpl.java` — 转换器，Entity ↔ Event

**核心流程**（以 `submitApplyOrder` 为例）：

```
1. 通过 Gateway 查询外部数据（公司信息）
2. 通过 Factory 创建聚合根（ApplyOrderEntity）
3. 调用 Repository 保存聚合根
4. 通过 Convert 转换事件 → MQ 发送
```

**DDD 知识点**：

| 概念 | 解释 | 对应代码 |
|------|------|----------|
| Application Service | 无状态的编排服务，协调领域对象完成用例 | `ApplyOrderServiceImpl` |
| Factory | 创建复杂领域对象的工厂 | `ApplyOrderFactory` |
| Convert/Assembler | 对象转换（Entity → DTO/Event） | `ApplyOrderConvert` |
| 构造函数注入 | DDD 推荐方式，依赖一目了然 | `@Autowired public ApplyOrderServiceImpl(...)` |

**面试要点**：
> 问：应用服务和领域服务的区别？
> 答：应用服务是"流程编排"，不包含业务规则。领域服务是"业务逻辑"，处理单个实体无法完成的跨实体业务。

> 问：为什么用构造函数注入而不是 @Autowired 字段注入？
> 答：构造函数注入让依赖在编译期可见，方便单元测试（可以直接 new 传 mock），也避免 NullPointerException。

---

### 4. 领域层 (Domain) — `my-ddd-demo-domain` ⭐ 核心

**定位**：DDD 的心脏。所有业务逻辑、业务规则都在这一层。**不依赖任何外部框架**。

**代码位置**：`my-ddd-demo-domain/src/main/java/com/viw/ddd/demo/domain/`

**关键文件**：

| 类别 | 文件 | 说明 |
|------|------|------|
| 聚合根 (Aggregate Root) | `ApplyOrderEntity.java` | 申请单聚合根，包含状态流转逻辑 |
| 值对象 (Value Object) | `ApplyOrderDetailVO.java`, `ApplyOrderExpressVO.java` | 不可变的描述性对象 |
| 仓储接口 (Repository) | `ApplyOrderRepository.java` | 持久化抽象，只有接口没有实现 |
| 网关接口 (Gateway) | `CompanyGateway.java`, `MqSender.java` | 防腐层，隔离外部系统 |

**DDD 知识点**：

| 概念 | 解释 | 代码体现 |
|------|------|----------|
| 聚合根 (Aggregate) | 一组相关对象的"根"，外部只能通过根访问内部对象 | `ApplyOrderEntity` |
| 实体 (Entity) | 有唯一标识的对象，生命周期中有状态变化 | `ApplyOrderEntity` 的状态字段 |
| 值对象 (VO) | 无标识的描述性对象，靠属性值判断相等 | `ApplyOrderDetailVO` |
| 领域事件 | 领域中发生的事情 | `ApplyOrderSubmittedEvent`（在 app 层） |
| 仓储 (Repository) | 聚合根的"家"，负责存取 | 接口在 domain，实现在 infra |
| 防腐层 (ACL) | 隔离外部系统，不污染领域模型 | `CompanyGateway` 接口 |
| 依赖倒置 (DIP) | 接口归领域层，实现归基础设施层 | Gateway/Repository 都是接口 |

**聚合根的状态流转**（本仓库核心示例）：

```
DRAFT → EXPRESSED → APPROVED → CREATE_BATCH
                                  ↓
                            INVOICE_FINISHED
                                  ↓
                            SENT_MAIL → SENT_EXPRESS
```

每个状态跳转都有校验（如 DRAFT 不能直接跳到 SENT_EXPRESS），业务规则在 Entity 内部。

**面试要点**：
> 问：Entity 和 Value Object 的区别？
> 答：Entity 有唯一标识（ID），生命周期中可变，靠 ID 判断相等。VO 没有 ID，不可变，靠属性值判断相等。

> 问：为什么 Repository 接口定义在 domain 层？
> 答：这是 DDD 的依赖倒置原则。领域层只需知道"我需要存取聚合根"，不需要知道底层是 MySQL 还是 MongoDB。基础设施层实现接口即可。

> 问：聚合设计的原则？
> 答：小聚合、通过 ID 引用其他聚合、最终一致性。一个事务只修改一个聚合根。

---

### 5. 基础设施层 (Infra) — `myb-ddd-demo-infra`

**定位**：纯技术实现层。数据库访问、RPC 调用、MQ 发送都在这里。

**代码位置**：`myb-ddd-demo-infra/src/main/java/com/viw/ddd/demo/infra/`

**关键文件**：

| 接口（domain定义） | 实现（infra这里） | 职责 |
|-------------------|-------------------|------|
| `ApplyOrderRepository` | `ApplyOrderRepositoryImpl` | 内存 Map 模拟数据库 |
| `CompanyGateway` | `CompanyGatewayImpl` | 模拟远程 RPC 调用 |
| `MqSender` | `MqSenderImpl` | 模拟 MQ 消息发送 |

**DDD 知识点**：

| 概念 | 解释 |
|------|------|
| DO (Data Object) | 与数据库表结构一一对应的对象，仅 infa 内部使用 |
| Entity ↔ DO 转换 | 领域对象和持久化对象的互相转换（`toEntity()` / `toDO()`） |
| Gateway 实现 | 封装外部 RPC/HTTP 调用细节，将外部 DO 转为领域 DTO |

**面试要点**：
> 问：Entity 和 DO 为什么要分开？
> 答：Entity 包含业务行为，DO 只是数据库映射。分开后可以独立演化（改表不一定改 Entity，加业务逻辑不一定改表）。这就是"持久化无关"。

### 6. 通用模块 (Common) — `my-ddd-demo-common`

**定位**：跨层共用的基础设施代码。不属于 DDD 四层中的任何一层，但所有层都可以依赖它。

**代码位置**：`my-ddd-demo-common/src/main/java/com/viw/ddd/demo/common/`

**关键文件**：

| 类别 | 文件 | 说明 |
|------|------|------|
| 业务异常 | `BusinessException.java` | code + message，统一异常体系 |
| 参数校验异常 | `ValidationException.java` | 参数不合法时抛出 |
| 状态枚举 | `ApplyOrderStatusEnum.java` | 替代 String 常量，类型安全 |
| 统一返回体 | `Result.java` | 所有 Controller 返回 `Result<T>` |
| 分页结果 | `PageResult.java` | 分页查询通用返回 |

**DDD 知识点**：
> Common 模块的定位常被讨论——它不是 DDD 战术设计的一部分，但实际项目中必不可少。关键是：Common 不包含业务逻辑，只提供技术性基础设施。

---

### 7. 适配层的扩展（Adapter）

除了 Facade/Controller，Adapter 层还承担这些角色：

| 场景 | 类 | 说明 |
|------|-----|------|
| 定时任务 | `ApplyOrderScheduler` | @Scheduled，模拟超时检查 |
| HTTP 回调 | `ApplyOrderCallbackController` | 接收第三方审批结果回调 |
| MQ 消费者 | `ApplyOrderEventConsumer` | 模拟 Kafka/RabbitMQ 消息消费 |
| 全局异常处理 | `GlobalExceptionHandler` | @RestControllerAdvice，统一异常→Result |

**统一模式**：所有外部触发（HTTP/MQ/定时）→ Adapter → App Service → Domain，不直接操作领域对象。

---

## 三、依赖方向总结

```
 正确方向（当前版本）：
  API → Domain（VO/DTO）
  Adapter → App + API
  App → Domain（接口：Repository/Gateway）
  Infra → Domain（实现接口）
  Common ← Adapter/App/Domain/Infra（公共依赖）
  Start → 所有模块（组装）
```

**核心原则**：Domain 层是 0 依赖的纯 POJO，所有箭头指向 Domain。

---

## 四、快速对照表：DDD 概念 → 本仓库代码

| DDD 概念 | 本仓库对应 | 位置 |
|----------|-----------|------|
| 聚合根 | `ApplyOrderEntity` | domain |
| 值对象 | `ApplyOrderDetailVO`, `ApplyOrderExpressVO` | domain |
| 仓储 | `ApplyOrderRepository`(接口) → `ApplyOrderRepositoryImpl`(实现) | domain/infra |
| 工厂 | `ApplyOrderFactory` | app |
| 领域服务 | （本仓库尚未实现） | domain |
| 领域事件 | `ApplyOrderSubmittedEvent`, `ExpressSentEvent` | app |
| 应用服务 | `ApplyOrderServiceImpl` | app |
| 命令 | `SubmitApplyOrderCommand`, `SendExpressCommand` | api |
| 查询门面 | `ApplyOrderQueryFacade` | api |
| 防腐层 | `CompanyGateway`(接口)→`CompanyGatewayImpl`(实现), `SubmitApplyOrderDTO`+`ApplyOrderAssembler` | domain/infra, app |
| 数据对象 | `ApplyOrderDO` | infra |
| 转换器 | `ApplyOrderConvertImpl` | app |
| MapStruct | `ApplyOrderAssembler`, `ApplyOrderDataAssembler` | app, infra |
| 适配器 | `ApplyOrderCommandFacadeImpl` | adapter |
| 异常处理 | `GlobalExceptionHandler` | adapter |
| 定时任务 | `ApplyOrderScheduler` | adapter |
| 回调/消费者 | `ApplyOrderCallbackController`, `ApplyOrderEventConsumer` | adapter |
| 业务异常 | `BusinessException` | common |
| 枚举 | `ApplyOrderStatusEnum` | common |
| 统一返回 | `Result<T>` | common |

---

## 五、动手练习建议

1. **跟踪一个请求**：从 `ApplyOrderCommandFacadeImpl` → `ApplyOrderServiceImpl.submitApplyOrder()` → `ApplyOrderEntity` → `ApplyOrderRepositoryImpl`，看完整链路
2. **加一个新状态**：在 `ApplyOrderEntity` 中加一个 `CANCELLED` 状态，补上状态流转校验
3. **换一个持久化方式**：把 `ApplyOrderRepositoryImpl` 从内存 Map 改成 MySQL（体会"接口不变、实现可换"）
4. **加一个领域服务**：当"创建申请单时需要校验信用额度"，这个逻辑跨了两个聚合，适合放到领域服务
