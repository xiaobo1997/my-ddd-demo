# DDD 核心概念速查

> 面试快速回忆用，每个概念一句话 + 代码示例 + 面试话术

---

## 1. 战略设计 vs 战术设计

| 层级 | 关注点 | 核心概念 |
|------|--------|----------|
| **战略设计** | 系统怎么划分 | 限界上下文、上下文映射、通用语言 |
| **战术设计** | 代码怎么写 | 聚合、实体、值对象、领域服务、领域事件、资源库 |

> 💡 面试话术：「战略设计解决"大泥球"问题——把系统拆成多个限界上下文；战术设计解决单个上下文内的代码组织——用聚合保证业务一致性。」

---

## 2. 实体（Entity）

**一句话**：有唯一标识、有生命周期、状态会变的业务对象。

```java
// ✅ 实体：有自己的 ID，状态会流转
public class ApplyOrderEntity {
    private Long id;                    // 唯一标识
    private OrderStatus status;         // 状态（会变）
    private LocalDateTime createdAt;    // 创建时间
    private LocalDateTime updatedAt;    // 修改时间

    public void approve() {
        if (this.status != OrderStatus.PENDING) {
            throw new IllegalStateException("只有待审批状态才能审批");
        }
        this.status = OrderStatus.APPROVED;
        this.updatedAt = LocalDateTime.now();
    }
}
```

> 💡 面试话术：「实体核心是**唯一标识 + 业务行为**。和传统贫血模型最大的区别是：实体自带行为方法，不是被 Service 改来改去的纯数据对象。」

---

## 3. 值对象（Value Object）

**一句话**：没有唯一标识、不可变、通过属性值判断相等的对象。

```java
// ✅ 值对象：没有 ID，通过属性值判断相等，通常设计为不可变
@Value  // Lombok：全字段构造 + getter + equals/hashCode
public class AddressVO {
    String province;
    String city;
    String district;
    String detail;
}

// 使用：两个 AddressVO 的 province/city/district/detail 都相同 → equals 为 true
```

> 💡 面试话术：「值对象没有 ID，**相等性由属性值决定**。实际项目中我把地址、金额范围、联系方式都建模为值对象。好处是天然线程安全，不用考虑并发问题。」

---

## 4. 聚合（Aggregate）与聚合根（Aggregate Root）

**一句话**：一组强关联的实体和值对象，只能通过聚合根访问。

```
聚合根 = 外部访问入口 + 一致性边界 + 事务边界

ApplyOrderEntity（聚合根）
    ├── DetailVO（值对象）        ← 订单明细
    ├── ExpressVO（值对象）        ← 物流信息
    └── List<AttachmentVO>（值对象集合） ← 附件列表

规则：
✅ 外部只能通过 ApplyOrderEntity 修改 Detail/Express
❌ 不能绕过聚合根直接改 DetailVO
✅ 一个事务只改一个聚合
```

```java
// ✅ 正确：通过聚合根修改
applyOrder.addDetail(new DetailVO(...));
applyOrder.sendExpress("SF123456");
repo.save(applyOrder);  // 整体持久化

// ❌ 错误：绕过聚合根
detailRepo.update(detail);  // 绕过了聚合根！
```

> 💡 面试话术：「聚合是我认为 DDD 最核心的战术模式。它解决两个问题：**1) 一致性边界**——一个事务只改一个聚合，跨聚合用最终一致性；**2) 不变量保护**——聚合根的状态变更方法里写校验逻辑，外部不能随便改。」

---

## 5. 领域服务（Domain Service）

**一句话**：不属于任何一个实体/值对象的业务逻辑，通常跨聚合操作。

```java
// ✅ 领域服务：跨聚合的资金冻结逻辑
public class FundFreezeService {
    
    // 不属于 AccountEntity，也不属于 FreezeOrderEntity
    public void freeze(Long accountId, BigDecimal amount) {
        AccountEntity account = accountRepo.findById(accountId);
        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException();
        }
        FreezeOrderEntity freezeOrder = new FreezeOrderEntity(accountId, amount);
        account.decreaseBalance(amount);          // 聚合1
        freezeOrderRepo.save(freezeOrder);         // 聚合2
        // 注：两个聚合修改通常用最终一致性（发领域事件），这里为了演示
    }
}
```

> 💡 面试话术：「什么时候用领域服务？**当一个业务操作不属于任何实体时**。比如转账——钱不是从 AccountEntity 流到 AccountEntity 的，而是有一个独立的'转账'概念。实际项目里我把额度校验、风控计算都放在领域服务里。」

---

## 6. 资源库（Repository）

**一句话**：聚合根的持久化抽象，接口在 domain 层，实现在 infra 层。

```java
// domain 层：只定义接口
public interface ApplyOrderRepository {
    ApplyOrderEntity findById(Long id);
    void save(ApplyOrderEntity entity);
    void update(ApplyOrderEntity entity);
}

// infra 层：具体实现（内存/MySQL/Redis 随便换）
@Repository
public class ApplyOrderRepositoryImpl implements ApplyOrderRepository {
    @Autowired
    private ApplyOrderMapper applyOrderMapper;  // MyBatis Mapper
    
    @Override
    public ApplyOrderEntity findById(Long id) {
        ApplyOrderDO orderDO = applyOrderMapper.selectById(id);
        DetailDO detailDO = detailMapper.selectByOrderId(id);
        ExpressDO expressDO = expressMapper.selectByOrderId(id);
        return ApplyOrderAssembler.toEntity(orderDO, detailDO, expressDO);
    }
    
    @Override
    public void save(ApplyOrderEntity entity) {
        ApplyOrderDO orderDO = ApplyOrderAssembler.toOrderDO(entity);
        DetailDO detailDO = ApplyOrderAssembler.toDetailDO(entity.getDetail());
        ExpressDO expressDO = ApplyOrderAssembler.toExpressDO(entity.getExpress());
        applyOrderMapper.insert(orderDO);
        detailMapper.insert(detailDO);
        expressMapper.insert(expressDO);
    }
}
```

> 💡 面试话术：「Repository 的核心价值是**依赖倒置**。domain 层只依赖接口，infra 层提供实现。换存储（MySQL→MongoDB）只需要换 infra 实现类，domain 层一行不改。」

---

## 7. 工厂（Factory）

**一句话**：负责创建复杂聚合，把创建逻辑从实体/Service 中抽离。

```java
// ✅ 工厂：封装复杂的聚合创建
@Component
public class ApplyOrderFactory {
    
    public ApplyOrderEntity createFromCommand(SubmitApplyOrderCommand command) {
        ApplyOrderEntity entity = ApplyOrderEntity.builder()
            .companyId(command.getCompanyId())
            .amount(command.getAmount())
            .status(OrderStatus.PENDING)
            .createdAt(LocalDateTime.now())
            .build();
        
        if (command.getDetail() != null) {
            DetailVO detail = DetailVO.create(command.getDetail());
            entity.setDetail(detail);
        }
        return entity;
    }
}
```

> 💡 面试话术：「工厂解决**聚合创建复杂度**。聚合里可能有 VO、有默认状态、有关联对象初始化，这些逻辑不应该散在 Service 里。实际项目里我每个聚合都配一个 Factory。」

---

## 8. 领域事件（Domain Event）

**一句话**：领域内发生的重要业务事实，用于解耦和异步通知。

```java
// ✅ 领域事件
public class ApplyOrderApprovedEvent {
    private Long orderId;
    private Long companyId;
    private LocalDateTime approvedAt;
    
    public ApplyOrderApprovedEvent(ApplyOrderEntity order) {
        this.orderId = order.getId();
        this.companyId = order.getCompanyId();
        this.approvedAt = LocalDateTime.now();
    }
}

// 发布事件
public class ApplyOrderServiceImpl implements ApplyOrderService {
    public void approve(Long id) {
        ApplyOrderEntity order = repo.findById(id);
        order.approve();
        repo.update(order);
        
        // 发布领域事件（解耦：审批后谁需要通知？不管）
        eventPublisher.publish(new ApplyOrderApprovedEvent(order));
    }
}
```

> 💡 面试话术：「领域事件是我的**解耦利器**。订单审批通过后，可能需要通知风控、同步到数据仓库、发短信——这些我不在主流程里串行做，而是发一个事件，谁关心谁订阅。也方便后续新需求接入，不改主流程。」

---

## 9. 防腐层（Anti-Corruption Layer, ACL）

**一句话**：隔离外部系统的模型污染，内部用 DDD 模型，外部用 DTO 转换。

```java
// domain 层：内部模型
public class ApplyOrderEntity {
    private Long id;
    private OrderStatus status;   // ✅ 领域枚举
    private Money amount;          // ✅ 值对象
}

// adapter/infra 层：防腐转换
@Component
public class ApplyOrderCommandConvert {
    // Command → DTO（API 层的 Command 不能直接进入 domain）
    public ApplyOrderDTO toDTO(SubmitApplyOrderCommand command) {
        return ApplyOrderDTO.builder()
            .companyId(Long.valueOf(command.getCompanyId()))
            .amount(Money.of(command.getAmount()))  // 防腐：String → Money
            .build();
    }
}
```

> 💡 面试话术：「防腐层的核心是**不让外部模型污染领域模型**。前端传来的 DTO 是贫血的、带校验注解的，领域 Entity 是充血的、带业务方法的——它们必须分开。实际项目里我用 MapStruct 自动做 Command→DTO 转换。」

---

## 10. CQRS（命令查询职责分离）

**一句话**：写操作走 Command 模型，读操作走 Query 模型，读写分离。

```java
// 写：通过聚合根
public class ApplyOrderCommandFacade {
    public void submit(SubmitApplyOrderCommand command) {
        ApplyOrderEntity entity = factory.createFromCommand(command);
        repo.save(entity);
    }
}

// 读：直接查表，不走聚合根
public class ApplyOrderQueryFacade {
    public ApplyOrderDetailVO query(Long id) {
        // 直接 SQL 查询，可以跨表 JOIN
        return orderQueryMapper.selectDetailById(id);
    }
}
```

> 💡 面试话术：「CQRS 的核心是**写和读的模型不同**。写必须走聚合保证一致性，读可以直接查表甚至走 ES。实际项目里我把 Command 走 Dubbo 写的接口，Query 走 HTTP 查的接口，两边独立优化。」

---

## 面试速记口诀

```
实体有 ID 会变化，值对象没 ID 不可变
聚合一根管一群，外部访问只能它
Repository 管持久化，接口在 domain 实现在 infra
工厂负责建聚合，领域服务跨聚合
防腐层转换外部模型，领域事件解耦通知
CQRS 读写分离，Command 走聚合 Query 直接查
```

---

## 参考资料

- Eric Evans《领域驱动设计》（蓝宝书）
- Vaughn Vernon《实现领域驱动设计》（红宝书）
- 本项目 `docs/DDD分层架构详解.md`（实战版）
- 本项目 `docs/面试高频DDD问题.md`（面试版）
