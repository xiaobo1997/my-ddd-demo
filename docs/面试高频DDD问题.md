# 面试高频 DDD 问题 —— 基于 my-ddd-demo 仓库

> 以本仓库代码为实例，用"说人话"的方式回答面试中常见的 DDD 问题。
> 每个问题都标注了对应的代码位置，可以直接打开看。

---

## Q1: 你理解的 DDD 是什么？和 MVC 有什么区别？

**一句话**：DDD 把业务逻辑放在第一位，MVC 把技术分层放在第一位。

**展开说**：

| | MVC | DDD |
|---|-----|-----|
| 核心 | 按技术职责分层（Controller/Service/DAO） | 按业务领域划分（订单域/用户域/风控域） |
| Service层 | 什么都往里堆，容易变成"大泥球" | 拆成应用服务（编排）+ 领域服务（业务规则） |
| 业务逻辑 | 散落在 Service/Utils 各处 | 收拢在 Entity/ValueObject/领域服务中 |
| 代码可读性 | 看代码才知道业务规则 | 看 Entity 就懂业务规则 |

**看本仓库**：
- `ApplyOrderEntity.java` 的 `setStatus()` 和状态流转方法 —— 业务规则在实体内部，不在 Service 里
- `ApplyOrderServiceImpl.java` 只做编排（"取数据→调实体→保存"），不写 if/else 业务判断

---

## Q2: 说说 Entity 和 Value Object 的区别？

**一句话**：Entity 靠 ID 认人，VO 靠长相认人。

| | Entity（实体） | Value Object（值对象） |
|---|---|---|
| 唯一标识 | 有 ID | 没有 ID |
| 可变性 | 可变（状态会流转） | 不可变（改了就是另一个对象） |
| 相等判断 | ID 相同即相等 | 所有属性相同才相等 |
| 生命周期 | 有（创建→修改→删除） | 无（属于某个 Entity） |

**看本仓库**：
- Entity：`ApplyOrderEntity` — 有 `id` 字段，状态从 DRAFT→EXPRESSED→... 
- VO：`ApplyOrderDetailVO` — 没有 ID，只是描述申请单的一批明细项

**面试追加金句**：
> "Entity 可以类比数据库的主表记录，VO 可以类比 JSON 里的嵌套对象。Entity 是'谁'，VO 是'什么样'。"

---

## Q3: 聚合（Aggregate）怎么设计？有什么原则？

**一句话**：聚合是一组必须保持一致的对象的"族长"，外部只能通过族长访问族员。

**四个原则**：

1. **小聚合** — 聚合越小越好，别把整个订单系统塞一个聚合
2. **通过 ID 引用** — 聚合之间不直接引用对象，只保存 ID
3. **最终一致性** — 跨聚合的操作不强求强一致，用领域事件异步处理
4. **一个事务只改一个聚合** — 这是聚合存在的根本原因

**看本仓库**：
- `ApplyOrderEntity` 就是一个聚合根（"申请单"聚合的根）
- 它内部包含 `ApplyOrderDetailVO` 和 `ApplyOrderExpressVO`（值对象，聚合内部成员）

**面试追加金句**：
> "很多人设计的聚合要么太大（整个订单流程一个聚合），要么太小（每个字段都是独立聚合）。原则是：需要强一致的数据放一个聚合，最终一致的数据拆成多个聚合。"

---

## Q4: Repository 为什么定义在 Domain 层，实现在 Infra 层？

**一句话**：这是依赖倒置（DIP），让业务逻辑不依赖数据库。

**看本仓库**：
```
domain/ApplyOrderRepository.java     ← 接口："我需要 save/findById/update"
infra/ApplyOrderRepositoryImpl.java  ← 实现："我用 ConcurrentHashMap 实现"
```

**如果反过来**（传统做法）：
- Service 直接依赖 DAO → 业务代码和数据库绑定
- 换数据库 = 改 Service → 高风险

**DDD 做法**：
- Service 只依赖 Repository 接口 → 换数据库只需换 Impl
- 测试时 mock Repository 接口即可，不需要真实数据库

---

## Q5: Gateway（防腐层）是什么？和 Repository 有什么区别？

**一句话**：Repository 管"自己家的数据"，Gateway 管"别人家的数据"。

| | Repository | Gateway |
|---|---|---|
| 访问对象 | 本限界上下文的聚合根 | 外部系统（其他限界上下文） |
| 操作类型 | 增删改查（CRUD） | 查询/调用（通常是读） |
| 返回类型 | 聚合根（Entity） | DTO（外部数据） |

**看本仓库**：
- `ApplyOrderRepository` — 存取"申请单"聚合根（自己家的数据）
- `CompanyGateway` — 查询"公司"信息（别人家的数据，通过 RPC 获取）

**为什么叫"防腐层"**：
> 外部系统的数据结构会变、会烂，Gateway 把外部数据翻译成领域层能理解的样子，隔离"腐败"。

---

## Q6: 应用服务和领域服务有什么区别？

**一句话**：应用服务是"导演"（编排），领域服务是"专家"（执行业务规则）。

| | 应用服务 | 领域服务 |
|---|---|---|
| 职责 | 编排流程、事务控制 | 执行业务规则、跨实体逻辑 |
| 有无状态 | 无状态 | 无状态 |
| 典型代码 | 取实体→调方法→保存 | 处理"两个实体协作"的业务 |

**看本仓库**：
- 应用服务：`ApplyOrderServiceImpl.submitApplyOrder()` — 查询公司→创建实体→保存→发MQ（纯编排）
- 领域服务：本项目未实现（典型场景："创建申请单时校验信用额度"涉及申请单实体+信用额度实体）

**面试追加金句**：
> "如果你发现一个 Service 方法里充满了 if/else 业务判断，就应该考虑抽到领域服务或实体方法里。"

---

## Q7: CQRS 是什么？你项目里怎么用的？

**一句话**：读写分离，Command 做写操作，Query 做读操作。

**看本仓库**（Command 侧）：
- `SubmitApplyOrderCommand` — "提交申请单"命令（写操作）
- `SendExpressCommand` — "发快递"命令（写操作）

**面试展开**：
> "CQRS 全称 Command Query Responsibility Segregation。简单说就是：查询不要走 Command 模型，Command 不要返回数据。进阶玩法是读写用不同的数据库，但这个仓库是教学模板，Command 只做写操作，不涉及查询分离。"

---

## Q8: 领域事件有什么用？怎么设计的？

**一句话**：领域中"发生了什么事"，通知其他系统/限界上下文。

**看本仓库**：
- `ApplyOrderSubmittedEvent` — "申请单已提交"事件
- `ExpressSentEvent` — "快递已发出"事件
- 事件在 `ApplyOrderServiceImpl` 中通过 `mqSender.send()` 发布

**三个作用**：
1. **解耦** — 订单域不用知道"谁关心申请单提交"，只管发事件
2. **最终一致性** — 跨聚合/跨系统的操作通过事件异步完成
3. **审计追溯** — 事件天然是操作日志

---

## Q9: 你怎么做 Entity ↔ DO 转换的？

**一句话**：入库前 Entity→DO，出库后 DO→Entity。

**看本仓库**：
- `ApplyOrderRepositoryImpl.toDO(entity)` — Entity → DO（保存时）
- `ApplyOrderRepositoryImpl.toEntity(doObj)` — DO → Entity（查询时）

**为什么要转**：
- Entity 有业务行为（状态流转方法），DO 只有 getter/setter
- 数据库字段名可能和 Entity 属性名不一致
- 数据库有技术字段（create_time/update_time），Entity 不关心

---

## Q10: 你的 DDD 分层是怎么规划依赖的？

**一句话**：Domain 是 0 依赖的核心，所有人都依赖它，它不依赖任何人。

```
正确方向（本仓库已修复）：
  API → Domain（Command引用VO）
  Adapter → App
  App → Domain（Repository/Gateway 接口）
  Infra → Domain（实现 Repository/Gateway）
  Start → 组装所有模块
```

**面试金句**：
> "核心原则是依赖倒置：领域层定义接口（我需要什么），基础设施层提供实现（怎么做到）。这样换数据库、换 RPC 框架，领域代码一行不改。"

---

## 快速自测清单

面试前快速过一遍，看着概念能说出代码位置：

- [ ] 聚合根 → `ApplyOrderEntity.java`
- [ ] 值对象 → `ApplyOrderDetailVO.java`
- [ ] 仓储 → `ApplyOrderRepository.java`（接口）+ `ApplyOrderRepositoryImpl.java`（实现）
- [ ] 工厂 → `ApplyOrderFactory.java`
- [ ] 应用服务 → `ApplyOrderServiceImpl.java`
- [ ] 防腐层 → `CompanyGateway.java`（接口）+ `CompanyGatewayImpl.java`（实现）
- [ ] 领域事件 → `ApplyOrderSubmittedEvent.java`
- [ ] Command → `SubmitApplyOrderCommand.java`
- [ ] DO → `ApplyOrderDO.java`
- [ ] 转换器 → `ApplyOrderConvertImpl.java`
- [ ] MQ 抽象 → `MqSender.java`（接口）+ `MqSenderImpl.java`（实现）
