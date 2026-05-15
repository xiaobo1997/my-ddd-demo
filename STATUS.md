# my-ddd-demo 项目状态快照

> 最后更新：2026-05-16 凌晨
> 用途：跨会话恢复上下文。GitHub 仓库即真相来源。

## 仓库概览

| 项目 | 值 |
|------|-----|
| 本地路径 | `~/myworkspace/amh/loan/my-ddd-demo` |
| GitHub | `github.com/xiaobo1997/my-ddd-demo`（公开） |
| 技术栈 | Java 8 + Maven + Spring Boot 2.7.18 + MapStruct 1.4.2 |
| Commits | 25 |
| 测试 | 31 个全过（domain 16 + app 5 + infra 10） |
| JaCoCo | `my-ddd-demo-coverage` 模块聚合报告 |

## 模块结构（7+1）

```
my-ddd-demo/
├── api/       接口层 — Facade / Command / QueryDTO
├── adapter/   适配层 — FacadeImpl / Scheduler / Callback / MQ Consumer / ExceptionHandler
├── app/       应用层 — Service / Factory / Convert / Assembler / DTO
├── domain/    领域层 — Entity / VO / Repository接口 / Gateway接口（核心，0依赖）
├── infra/     基础设施层 — RepositoryImpl / GatewayImpl / DO / DataAssembler
├── common/    通用模块 — Exception / Enum / Result<T> / PageResult<T>
├── start/     启动模块 — Spring Boot 启动
└── coverage/  覆盖率聚合 — JaCoCo report-aggregate（packaging=pom）
```

## 已完成的 DDD 核心功能

- [x] 聚合根 `ApplyOrderEntity` + 状态流转（7 状态 + 校验）
- [x] 值对象 `DetailVO` / `ExpressVO`
- [x] Repository 接口在 domain，实现在 infra（依赖倒置）
- [x] Gateway 防腐层接口在 domain，实现在 infra
- [x] CQRS 读写分离：CommandFacade(写) + QueryFacade(读)
- [x] 防腐层：Command → MapStruct → DTO → Service
- [x] MapStruct 映射：Command→DTO / Entity↔DO / DetailVO↔DetailDO / ExpressVO↔ExpressDO
- [x] 聚合根一致性持久化：三张内存表整体存取（db + detailDb + expressDb）
- [x] CRUD = 查询 + 修改，无物理删除
- [x] 定时任务 / HTTP回调 / MQ消费者（adapter 层）
- [x] 全局异常处理（common 定义 + adapter 处理）
- [x] 统一返回体 `Result<T>`
- [x] API 模块 Maven 发布配置（source-plugin，distributionManagement 注释说明）
- [x] 单元测试 31 个（JUnit5 + Mockito）
- [x] JaCoCo 覆盖率独立为 coverage 聚合模块
- [x] 三张深色 SVG 架构图（docs/diagrams/）
- [x] DDD 教学文档（docs/DDD分层架构详解.md + docs/面试高频DDD问题.md）
- [x] infra 模块拼写修复（myb → my）

## 待完成（P3）

- [ ] Adapter 层 ApplyOrderCommandFacadeImpl 加 Spring DI（当前字段未注入）
- [ ] 补全 SendExpressCommand 防腐处理（当前直接透传）
- [ ] 数据库持久化替代内存 Map（展示"接口不变实现可换"）
- [ ] 领域服务示例（跨聚合业务逻辑）
- [ ] 集成测试

## 编程 Agent 配置

| Agent | 状态 | 调用方式 |
|-------|------|----------|
| Claude Code | ✅ 可用 | `bash ~/tools/claude-pro.sh --permission-mode acceptEdits -p "任务"` |
| Codex | ❌ 暂停 | Moon Bridge 502 |

小任务（2-3 文件）稳定，大任务超时需拆分。

## 已加载的 Hermes Skills（12 个）

claude-code / subagent-driven-development / writing-plans / spike /
systematic-debugging / github-pr-workflow / test-driven-development /
requesting-code-review / architecture-diagram / excalidraw / humanizer / ddd-project

## 面试资料路径

- 项目资料：`~/myworkspace/work/资料/mb/`
- 八股文：`~/myworkspace/git/interview/docs/`
- 简历：`~/myworkspace/work/资料/面试/简历/最新的简历/肖海波简历06.pdf`
- 满帮话术：`~/myworkspace/work/资料/mb/面试话术-满帮项目.md`

## 股票监控

4 个 cron 任务已暂停（周末），周一可用：
- 每日复盘 / 盘中监控(5分钟) / 盘前简报 / 收盘复盘

## 常用命令

```bash
mvn clean test                    # 跑测试
mvn verify                         # 生成覆盖率聚合报告
open my-ddd-demo-coverage/target/site/jacoco-aggregate/index.html
open docs/diagrams/business-architecture.html
```
