# my-ddd-demo

基于领域驱动设计（DDD）的多模块 Java 工程脚手架，使用 Java 8 + Maven 构建。

## 项目结构

```
my-ddd-demo/
├── my-ddd-demo-api/          # 对外接口层 — 定义 Command 门面契约和 DTO
├── my-ddd-demo-app/          # 应用服务层 — 编排业务流程、工厂、转换器、事件
├── my-ddd-demo-domain/       # 领域层 — 核心业务实体、值对象、仓储接口
├── my-ddd-demo-adapter/      # 适配层 — 实现 API 门面，桥接外部请求到应用服务
├── myb-ddd-demo-infra/       # 基础设施层 — 持久化实现、远程 RPC 网关、MQ 发送
├── my-ddd-demo-start/        # 启动模块 — 应用入口、配置
├── pom.xml                   # 父 POM，模块管理与依赖版本控制
└── AGENTS.md                 # AI 辅助开发指南
```

## 技术栈

- **Java 8** — 语言版本
- **Maven** — 项目构建
- **Lombok** — 简化 POJO
- **Fastjson** — JSON 序列化
- **DDD** — 领域驱动设计分层架构

## 业务上下文

演示场景为**申请单（ApplyOrder）管理**，包含以下核心流程：

1. **提交申请单** — 提交申请、校验公司信息、保存并发送事件
2. **快递派送** — 录入快递单号、更新状态、发送事件
3. **状态流转** — 创建→审批→开票→寄送的全生命周期

## 快速开始

```bash
# 编译所有模块
mvn clean compile

# 运行测试
mvn clean test

# 打包
mvn clean package
```

## 模块依赖关系

```
adapter → api + app
app     → api + domain + infra
domain  → (纯 POJO，无外部依赖)
infra   → domain
```

> **注意**：当前 app 层直接依赖 infra 层（违反 DDD 规范），后续重构会将 Gateway 接口抽离到 domain 层。

## TODO / 演进计划

- [ ] 修复包名拼写错误（domain → domain）
- [ ] 统一模块命名（myb → my）
- [ ] 引入 Spring Boot 依赖与 IoC 容器
- [ ] 补齐领域实体业务方法
- [ ] 实现仓储层持久化逻辑
- [ ] 添加单元测试与集成测试
- [ ] 优化 DDD 架构分层合规性

---

*项目脚手架生成，逐步演进中。*
