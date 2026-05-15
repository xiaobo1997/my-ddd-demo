# my-ddd-demo

基于领域驱动设计（DDD）的多模块 Java 工程**教学脚手架**，使用 Java 8 + Maven 构建。

## 🎯 项目目标

**展示一个完整的 DDD 工程模板长什么样。** 每个类都用注释注明：

- 这个类属于 DDD 的**哪一层**（API/Application/Domain/Infrastructure/Adapter）
- 它在 DDD 中扮演的**角色**是什么（聚合根/值对象/仓储/工厂/应用服务/转换器等）
- 它和相邻组件如何**协作**

让没接触过 DDD 的程序员一看就能理解分层结构和职责划分。

## 🏗 模块结构（DDD 经典四层架构）

```
┌─────────────────────────────────────┐
│  api 接口层                          │
│  ApplyOrderCommandFacade (门面接口)   │
│  SubmitApplyOrderCommand (命令对象)    │
├─────────────────────────────────────┤
│  adapter 适配层                      │
│  ApplyOrderCommandFacadeImpl (实现)   │
├─────────────────────────────────────┤
│  app 应用层                          │
│  ApplyOrderService (应用服务)         │
│  ApplyOrderFactory (工厂)            │
│  ApplyOrderConvert (转换器)          │
│  *SubmittedEvent / *SentEvent (事件) │
├─────────────────────────────────────┤
│  domain 领域层 (核心)                 │
│  ApplyOrderEntity (聚合根 + 业务方法) │
│  ApplyOrderRepository (仓储接口)      │
│  ApplyOrderDetailVO / ExpressVO (值对象)│
├─────────────────────────────────────┤
│  infra 基础设施层                     │
│  ApplyOrderRepositoryImpl (仓储实现)   │
│  ApplyOrderDO / DetailDO / ExpressDO │
│  CompanyGateway (外部网关)           │
│  MqSender (消息发送器)               │
└─────────────────────────────────────┘
```

## 📖 核心 DDD 概念速览

| 概念 | 本工程中的体现 | 说明 |
|------|--------------|------|
| **聚合根 (Aggregate Root)** | `ApplyOrderEntity` | 有唯一ID，聚合内的入口，保证数据一致性 |
| **实体 (Entity)** | `ApplyOrderEntity` | 有唯一标识（id），状态可变 |
| **值对象 (VO)** | `ApplyOrderDetailVO`, `ApplyOrderExpressVO` | 无唯一标识，描述性，依附于实体 |
| **仓储 (Repository)** | `ApplyOrderRepository` | 聚合根的持久化抽象，接口在 domain，实现 infra |
| **工厂 (Factory)** | `ApplyOrderFactory` | 封装复杂实体创建逻辑 |
| **应用服务 (App Service)** | `ApplyOrderService` | 编排业务流程，不包含业务规则 |
| **领域事件 (Domain Event)** | `*SubmittedEvent`, `*SentEvent` | 已发生的事情，通过 MQ 通知其他系统 |
| **Gateway (网关)** | `CompanyGateway` | 封装对外部系统的调用 |
| **数据对象 (DO)** | `*DO` | 与数据库表对应的持久化对象 |
| **命令对象 (Command)** | `*Command` | 封装一次业务操作的输入参数 |

## 🔄 一次完整的请求流程

```
submitApplyOrder(Command)
     │
     ▼
ApplyOrderCommandFacadeImpl (Adapter)
     │  透传 Command
     ▼
ApplyOrderServiceImpl (Application)
     │  ├─ CompanyGateway.findByCompanyId() → 查外部公司信息
     │  ├─ ApplyOrderFactory.createApplyOrder() → 创建聚合根
     │  ├─ ApplyOrderRepository.save() → 持久化聚合根
     │  └─ MqSender.send(Event) → 发布领域事件
     ▼
完成
```

## 🛠 技术栈

- **Java 8** — 语言版本
- **Maven** — 项目构建
- **Lombok** — 简化 POJO
- **Fastjson** — JSON 序列化
- **Spring Boot 2.7.18** — IoC 容器 + Web 支持（Java 8 兼容版本）

## 🚀 快速开始

```bash
mvn clean compile
```

---

*DDD 教学脚手架，注释比代码更重要。*
