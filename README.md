# my-ddd-demo

基于领域驱动设计（DDD）的多模块 Java 工程**教学脚手架**，使用 Java 8 + Maven 构建。

## 项目目标

**展示一个完整的 DDD 工程模板长什么样。** 每个类都用注释注明：

- 这个类属于 DDD 的**哪一层**
- 它在 DDD 中扮演的**角色**是什么
- 它和相邻组件如何**协作**

## 模块全景

| 模块 | 层 | 职责 | 关键类 |
|------|---|------|--------|
| **my-ddd-demo-api** | 接口层 | 对外契约：Command / Facade 接口 / QueryDTO | `ApplyOrderCommandFacade`, `ApplyOrderQueryFacade`, `*Command`, `*QueryDTO` |
| **my-ddd-demo-adapter** | 适配层 | 外部入口适配：Controller / 定时任务 / 回调 / MQ消费者 / 异常处理 | `*FacadeImpl`, `*Scheduler`, `*CallbackController`, `*EventConsumer`, `GlobalExceptionHandler` |
| **my-ddd-demo-app** | 应用层 | 流程编排 + 防腐层：Service / Factory / Convert / Assembler | `ApplyOrderServiceImpl`, `ApplyOrderQueryServiceImpl`, `*Assembler` (MapStruct), `*Factory` |
| **my-ddd-demo-domain** | 领域层 | 核心：Entity / VO / Repository接口 / Gateway接口 | `ApplyOrderEntity`, `ApplyOrderDetailVO`, `*Repository`, `*Gateway` |
| **my-ddd-demo-infra** | 基础设施层 | 技术实现：Repository实现 / Gateway实现 / MQ实现 / DO | `ApplyOrderRepositoryImpl`, `*GatewayImpl`, `*DO`, `ApplyOrderDataAssembler` |
| **my-ddd-demo-common** | 通用模块 | 跨层共用：异常 / 枚举 / 基础DTO | `BusinessException`, `ApplyOrderStatusEnum`, `Result<T>`, `PageResult<T>` |
| **my-ddd-demo-start** | 启动模块 | 组装+启动：Spring Boot 启动类 | `Main` |

**依赖方向**（箭头 = 依赖）：
```
start → adapter → app → domain ← infra
                  ↘ common ↙
api     →     domain
```

## DDD 核心概念速览

| 概念 | 本工程对应 | 层 |
|------|----------|-----|
| 聚合根 (Aggregate Root) | `ApplyOrderEntity` | domain |
| 值对象 (VO) | `ApplyOrderDetailVO`, `ApplyOrderExpressVO` | domain |
| 仓储 (Repository) | `ApplyOrderRepository` → `ApplyOrderRepositoryImpl` | domain/infra |
| 防腐层 (ACL) | `CompanyGateway` → `CompanyGatewayImpl` | domain/infra |
| CQRS 命令 (Command) | `SubmitApplyOrderCommand`, `SendExpressCommand` | api |
| CQRS 查询 (Query) | `ApplyOrderQueryFacade`, `ApplyOrderQueryDTO` | api |
| 防腐 DTO | `SubmitApplyOrderDTO` (Command→DTO 隔离) | app |
| MapStruct 映射 | `ApplyOrderAssembler` (Command→DTO), `ApplyOrderDataAssembler` (DO↔Entity) | app/infra |
| 应用服务 | `ApplyOrderServiceImpl` (写), `ApplyOrderQueryServiceImpl` (读) | app |
| 领域事件 | `ApplyOrderSubmittedEvent`, `ExpressSentEvent` | app |
| 工厂 | `ApplyOrderFactory` | app |
| 枚举 | `ApplyOrderStatusEnum` | common |
| 异常 | `BusinessException`, `ValidationException` | common |
| 统一返回 | `Result<T>` | common |
| 全局异常处理 | `GlobalExceptionHandler` | adapter |
| 定时任务 | `ApplyOrderScheduler` | adapter |
| HTTP 回调 | `ApplyOrderCallbackController` | adapter |
| MQ 消费者 | `ApplyOrderEventConsumer` | adapter |

## 两种完整请求流程

### 命令流程（写操作，CQRS Command 侧）
```
外部 RPC/HTTP 请求
    │ SubmitApplyOrderCommand (api)
    ▼
ApplyOrderCommandFacadeImpl (adapter)
    │ 防腐: Command → DTO (MapStruct)
    ▼
ApplyOrderServiceImpl (app)
    │ ├─ CompanyGateway.findByCompanyId()
    │ ├─ ApplyOrderFactory.createApplyOrder()
    │ ├─ ApplyOrderRepository.save()
    │ └─ MqSender.send(Event)
    ▼
完成
```

### 查询流程（读操作，CQRS Query 侧）
```
外部 HTTP 请求
    │ id (Long)
    ▼
ApplyOrderQueryFacadeImpl (adapter)
    │ 转发
    ▼
ApplyOrderQueryServiceImpl (app)
    │ Repository.findById(id) → Entity → QueryDTO
    ▼
返回 ApplyOrderQueryDTO (api)
```

## 定时任务 / 回调 / MQ 消费者定位

| 场景 | 放哪层 | 原因 |
|------|--------|------|
| 定时任务 | adapter/scheduler | 外部触发，类似 Controller |
| HTTP 回调 | adapter/callback | 第三方入口，属于适配层 |
| MQ 消费者 | adapter/consumer | 消息适配，和 HTTP 同级 |
| 异常定义 | common | 所有层共用 |
| 全局异常处理 | adapter/advice | @RestControllerAdvice 是技术细节 |

> 核心原则：**所有外部触发（HTTP/MQ/定时）都走 adapter，统一调用 app 层服务。**

## 发布 API 包到 Maven 仓库

### 使用场景
你的团队开发了 `my-ddd-demo-api` 模块，其他团队需要调用你的 RPC 接口。
把 API 包发布到公司内部 Maven 仓库（如 Nexus），其他团队直接引入依赖即可。

### 发布步骤
```bash
# 只发布 api 模块
mvn clean deploy -pl my-ddd-demo-api -am -DskipTests
```

### 其他团队引入
```xml
<dependency>
    <groupId>com.viw</groupId>
    <artifactId>my-ddd-demo-api</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```
引入后即可通过 Dubbo / gRPC / HTTP 调用 `ApplyOrderCommandFacade` 等方法。

> 父 pom.xml 中有 `distributionManagement` 配置说明（已注释），需替换为公司实际仓库地址。

## 技术栈

- **Java 8** — 语言版本
- **Maven** — 项目构建 + 发布
- **Spring Boot 2.7.18** — IoC + Web
- **MapStruct 1.4.2** — 编译期对象映射（Command→DTO / DO↔Entity）
- **Lombok** — 简化 POJO
- **Fastjson** — JSON 序列化

## 快速开始

```bash
mvn clean compile
```

## 文档

- [DDD 分层架构详解](docs/DDD分层架构详解.md) — 逐层讲解 + 代码对照
- [面试高频 DDD 问题](docs/面试高频DDD问题.md) — 10+ 题 + 本仓库实例

---

*DDD 教学脚手架，注释比代码更重要。*
