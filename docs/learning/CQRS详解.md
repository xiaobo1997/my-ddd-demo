# CQRS 详解 — 命令查询职责分离

> CQRS = Command Query Responsibility Segregation  
> 写操作走 Command 模型，读操作走 Query 模型，两者完全分离

---

## 一、为什么需要 CQRS？

### 传统 CRUD 的问题

```java
// ❌ 传统三层架构：读写用同一个模型
@RestController
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping("/order")          // 写
    public Result create(@RequestBody CreateOrderRequest req) {
        return orderService.create(req);  // req → Entity → DB
    }

    @GetMapping("/order/{id}")      // 读
    public OrderDetailVO get(@PathVariable Long id) {
        OrderEntity entity = orderRepo.findById(id);
        // 问题：Entity 有很多字段（20+），但列表只需要 5 个字段
        // 问题：详情页需要 JOIN 5 张表，Entity 模型根本承载不了
        // 问题：读多写少，但读操作也得走聚合根加载全部数据
        return convert(entity);
    }
}
```

痛点：
1. **模型冲突** — 写关注一致性，读关注性能，同一模型无法兼顾
2. **聚合根太重** — 每次查询都加载完整聚合（包括 VO、关联对象），浪费性能
3. **列表查询灾难** — 分页列表要 JOIN 多表，Entity 模型根本承载不了
4. **扩展困难** — 读需求变化频繁（加字段、改排序），改 Entity 影响写操作

---

## 二、CQRS 核心思想

```
                    ┌──────────────┐
    POST /order ──▶ │ Command 模型  │ ──▶ 聚合根 ──▶ Repository ──▶ DB（写库）
                    │  (写操作)     │
                    └──────────────┘

                    ┌──────────────┐
    GET /order  ──▶ │  Query 模型   │ ──▶ 直接 SQL ──▶ DB（读库/ES）
                    │  (读操作)     │
                    └──────────────┘

    ✅ 读写模型独立演进
    ✅ 读写数据库可以分离（主从）
    ✅ 读可以走缓存/ES/视图，和写完全解耦
```

---

## 三、本项目的 CQRS 实现

### 3.1 目录结构

```
api/
├── ApplyOrderCommandFacade.java    ← 命令门面接口（写）
└── ApplyOrderQueryFacade.java      ← 查询门面接口（读）

app/
├── ApplyOrderServiceImpl.java      ← 命令服务（走聚合根）
└── ApplyOrderQueryServiceImpl.java ← 查询服务（直接查表）

adapter/
└── impl/
    ├── ApplyOrderCommandFacadeImpl.java  ← 命令门面实现
    └── ApplyOrderQueryFacadeImpl.java    ← 查询门面实现
```

### 3.2 写链路（Command）

```java
// 1️⃣ API 层：命令门面接口
public interface ApplyOrderCommandFacade {
    Long submitApplyOrder(SubmitApplyOrderCommand command);
    void sendExpress(SendExpressCommand command);
}

// 2️⃣ Adapter 层：防腐转换 + 委托
@Component
public class ApplyOrderCommandFacadeImpl implements ApplyOrderCommandFacade {

    @Autowired private ApplyOrderAssembler assembler;
    @Autowired private ApplyOrderService service;

    @Override
    public Long submitApplyOrder(SubmitApplyOrderCommand command) {
        SubmitApplyOrderDTO dto = assembler.toDTO(command);  // 防腐
        return service.submitApplyOrder(dto);                 // 委托
    }
}

// 3️⃣ App 层：走聚合根（保证一致性）
@Service
public class ApplyOrderServiceImpl implements ApplyOrderService {
    @Override
    public Long submitApplyOrder(SubmitApplyOrderDTO dto) {
        CompanyDTO company = gateway.findByCompanyId(dto.getCompanyId());
        ApplyOrderEntity entity = ApplyOrderFactory.createApplyOrder(dto);
        entity.validate();  // 聚合根内校验
        return repository.save(entity);  // 整体持久化
    }
}
```

### 3.3 读链路（Query）

```java
// 1️⃣ API 层：查询门面接口
public interface ApplyOrderQueryFacade {
    ApplyOrderDetailVO getDetail(Long id);
    PageResult<ApplyOrderListVO> list(ApplyOrderQuery query);
}

// 2️⃣ Adapter 层
@Component
public class ApplyOrderQueryFacadeImpl implements ApplyOrderQueryFacade {

    @Autowired private ApplyOrderQueryService queryService;

    @Override
    public ApplyOrderDetailVO getDetail(Long id) {
        return queryService.getDetail(id);  // 直接查，不过聚合根
    }

    @Override
    public PageResult<ApplyOrderListVO> list(ApplyOrderQuery query) {
        return queryService.list(query);
    }
}

// 3️⃣ App 层：直接 SQL，不加载聚合根
@Service
public class ApplyOrderQueryServiceImpl implements ApplyOrderQueryService {

    @Autowired private ApplyOrderQueryMapper queryMapper;  // 专用的查询 Mapper

    @Override
    public ApplyOrderDetailVO getDetail(Long id) {
        // 一条 SQL JOIN 所有需要的表，不走 Repository
        return queryMapper.selectDetailById(id);
    }

    @Override
    public PageResult<ApplyOrderListVO> list(ApplyOrderQuery query) {
        List<ApplyOrderListVO> list = queryMapper.selectList(query);
        long total = queryMapper.countList(query);
        return PageResult.of(list, total);
    }
}
```

---

## 四、CQRS 的进阶形态

| 级别 | 描述 | 适用场景 |
|------|------|----------|
| **L1: 模型分离** | 同一库，读用 VO/QueryModel，写用 Entity | 中小项目入门 |
| **L2: 数据库读写分离** | 写走主库，读走从库 | 读多写少、有主从架构 |
| **L3: 读模型独立存储** | 读走 ElasticSearch / Redis / 物化视图 | 复杂搜索、高并发读 |
| **L4: Event Sourcing** | 写存事件流，读投影到视图 | 审计要求高、溯源需求 |

本项目实现的是 **L1**，展示核心思想。

---

## 五、面试高频问题

### Q1: CQRS 和 DDD 的关系？

CQRS 是战术模式，DDD 是整体方法论。DDD 的聚合根天然适合 CQRS：
- 聚合根保证写的强一致性 → Command 模型
- 读不需要一致性 → 可以绕过聚合根直接查

### Q2: 读写模型不一致怎么办？（数据同步）

```
写操作 → 发领域事件 → 异步更新读模型
例：
  订单创建 → 发 ApplyOrderCreatedEvent → 消费者更新 ES 索引
```

实际项目中的方案：
- 简单场景：写完直接查主库（允许短暂不一致）
- 复杂场景：Canal 监听 binlog → 同步到 ES

### Q3: 什么时候不该用 CQRS？

- 业务简单（CRUD 足够）、团队小（增加复杂度）
- 强一致性读要求（写完必须立刻读到）→ 不适合异步
- 没有读写性能瓶颈 → 过度设计

> 💡 面试话术：「我在满帮项目里实际上没叫 CQRS 这个名字，但做法是一样的。Command 接口走 Dubbo（RPC 写操作），Query 接口走 HTTP（读操作），数据库读写分离，读接口做了 Redis 缓存——这就是 CQRS 的落地。」

---

## 六、和传统三层对比

| 维度 | 传统三层 | CQRS |
|------|----------|------|
| 写操作 | Service → DAO → DB | Command → 聚合根 → Repository → DB |
| 读操作 | Service → DAO → DB | Query → 专用 Mapper → DB/缓存 |
| 模型 | 一个 Entity/DTO 通吃 | CommandDTO / QueryVO 分离 |
| 性能优化 | 读也加载完整 Entity | 读只查需要的字段 |
| 扩展性 | 加字段影响读写两边 | 读写独立演进 |
