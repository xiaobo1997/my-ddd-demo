# DDD 四层架构 vs 传统三层架构

> 面试中最常见的问题：「你为什么要用 DDD？和 MVC 三层有什么区别？」

---

## 一、架构对比图

```
传统三层（MVC）                    DDD 四层

┌─────────────────┐              ┌─────────────────────┐
│   Controller    │              │   Interface (API)    │ ← 接口定义
├─────────────────┤              ├─────────────────────┤
│    Service      │ ← 所有逻辑   │   Adapter (适配层)   │ ← 协议适配
│    (臃肿)       │    都在这里   ├─────────────────────┤
├─────────────────┤              │   Application (应用)  │ ← 流程编排
│      DAO        │              ├─────────────────────┤
└─────────────────┘              │   Domain (领域)       │ ← 核心业务
                                 ├─────────────────────┤
                                 │   Infrastructure     │ ← 技术实现
                                 └─────────────────────┘
```

---

## 二、核心区别

| 维度 | 传统三层 | DDD 四层 |
|------|----------|----------|
| **关注点** | 技术分层（Controller/Service/DAO） | 业务分层（领域/应用/适配/基础设施） |
| **业务逻辑** | 散落在 Service 层（贫血模型） | 集中在 Domain 层（充血模型） |
| **依赖方向** | Service → DAO（上层依赖下层） | Domain 独立（0 依赖），Adapter → App → Domain ← Infra（依赖倒置） |
| **模型** | Entity = 数据库表映射 | Entity = 业务对象（有行为） |
| **复用性** | Service 跨模块调用（耦合） | 限界上下文隔离，通过 API/事件通信 |
| **变更影响** | 改表结构 → 全链路改 | 改存储 → 只改 Infra 层 |

---

## 三、代码对比：同样的「创建订单」

### 传统三层

```java
// Controller
@RestController
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping("/order")
    public Result create(@RequestBody CreateOrderRequest req) {
        return orderService.create(req);
    }
}

// Service（所有逻辑堆在这里）
@Service
public class OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private CompanyService companyService;  // 远程调用也在这里

    public Result create(CreateOrderRequest req) {
        // 参数校验
        if (req.getAmount() == null || req.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("金额不能为空");
        }
        // 查询公司
        Company company = companyService.getById(req.getCompanyId());
        // 组装数据
        Order order = new Order();
        order.setCompanyId(req.getCompanyId());
        order.setAmount(req.getAmount());
        order.setStatus("PENDING");
        order.setCreatedAt(new Date());
        // 插入数据库
        orderMapper.insert(order);
        // 发消息
        mqProducer.send(JSON.toJSONString(order));
        return Result.success(order.getId());
    }
}

// Entity（贫血模型 — 只有 getter/setter）
@Data
public class Order {
    private Long id;
    private Long companyId;
    private BigDecimal amount;
    private String status;
    private Date createdAt;
    // 没有任何业务方法！
}
```

### DDD 四层

```java
// ========== API 层：接口契约 ==========
public interface OrderCommandFacade {
    Long submitOrder(SubmitOrderCommand command);
}

// ========== Adapter 层：防腐 + 委托 ==========
@Component
public class OrderCommandFacadeImpl implements OrderCommandFacade {
    @Autowired private OrderAssembler assembler;
    @Autowired private OrderApplicationService appService;

    public Long submitOrder(SubmitOrderCommand command) {
        SubmitOrderDTO dto = assembler.toDTO(command);  // 防腐转换
        return appService.submitOrder(dto);
    }
}

// ========== Application 层：流程编排（无业务逻辑） ==========
@Service
public class OrderApplicationService {
    @Autowired private OrderRepository repository;
    @Autowired private CompanyGateway gateway;    // domain 接口
    @Autowired private DomainEventPublisher publisher;

    public Long submitOrder(SubmitOrderDTO dto) {
        CompanyDTO company = gateway.findById(dto.getCompanyId());  // 防腐
        OrderEntity order = OrderFactory.create(dto);                // 工厂
        order.validate();                                            // 领域校验
        repository.save(order);                                      // 仓储
        publisher.publish(new OrderSubmittedEvent(order));           // 事件
        return order.getId();
    }
}

// ========== Domain 层：核心业务（充血模型） ==========
public class OrderEntity {
    private Long id;
    private OrderStatus status;      // 领域枚举，不是 String
    private Money amount;            // 值对象，不是 BigDecimal
    private LocalDateTime createdAt;

    // 业务方法 —— 自带校验
    public void validate() {
        if (this.amount == null || this.amount.isNegative()) {
            throw new InvalidOrderException("金额无效");
        }
        if (this.status != null) {
            throw new InvalidOrderException("新订单状态应为空");
        }
        this.status = OrderStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public void approve() {
        if (this.status != OrderStatus.PENDING) {
            throw new IllegalStateException("只有待审批的订单才能审批");
        }
        this.status = OrderStatus.APPROVED;
    }
}

// Domain 层只定义接口，不依赖具体实现
public interface OrderRepository {
    OrderEntity findById(Long id);
    void save(OrderEntity entity);
}

public interface CompanyGateway {
    CompanyDTO findById(Long companyId);
}

// ========== Infra 层：具体实现 ==========
@Repository
public class OrderRepositoryImpl implements OrderRepository {
    @Autowired private OrderMapper orderMapper;
    @Autowired private OrderDetailMapper detailMapper;

    public void save(OrderEntity entity) {
        // Entity → DO → DB（聚合根整体存取）
        orderMapper.insert(OrderAssembler.toOrderDO(entity));
        detailMapper.insert(OrderAssembler.toDetailDO(entity.getDetail()));
    }
}
```

---

## 四、什么时候用 DDD？

| 场景 | 推荐 | 理由 |
|------|------|------|
| 简单 CRUD 后台管理 | ❌ 不用 | 三层够用，DDD 过度设计 |
| 复杂业务流程（审批流、风控） | ✅ 用 | DDD 能承载复杂度 |
| 微服务拆分 | ✅ 用 | 限界上下文天然映射微服务 |
| 团队 3 人以下 | ❌ 不用 | 学习成本高、维护成本高 |
| 金融/保险/电商核心域 | ✅ 用 | 业务逻辑复杂、变更频繁 |

---

## 五、面试常见追问

### Q: DDD 的缺点是什么？

1. **学习成本高** — 团队要理解实体/值对象/聚合/仓储/工厂/领域服务等概念
2. **代码量大** — 同样功能 DDD 比三层多 30-50% 的代码（多了 Adapter、Assembler、DTO 转换等）
3. **简单业务不划算** — CRUD 管理后台用 DDD 是杀鸡用牛刀
4. **ORM 兼容差** — JPA/Hibernate 的自动级联和 DDD 聚合整体存取的理念冲突

### Q: 你们团队真的按这个来吗？

> 💡 面试话术：「不是教条式的 DDD。满帮项目里，核心的信贷流程（授信→用信→还款→逾期）严格按 DDD 走，因为业务逻辑复杂、变更多。但简单的配置管理、字典查询还是传统三层写法。关键是'核心域用 DDD，支撑域用三层'。」

### Q: DDD 和微服务的关系？

限界上下文 ≈ 微服务边界。DDD 的战略设计（限界上下文、上下文映射）直接指导微服务拆分。先做 DDD 建模，再按限界上下文切微服务——这种切法最不容易后悔。
