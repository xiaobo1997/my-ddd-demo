# DDD CQRS + MapStruct + 防腐层改造任务清单

逐一完成以下任务，每完成1-2个就 git commit（中文message）。最后 mvn compile 必须通过。

## 任务1：MapStruct 依赖配置
- 父pom.xml properties 加 `<mapstruct.version>1.4.2.Final</mapstruct.version>`
- 父pom dependencyManagement 加 mapstruct + mapstruct-processor
- app/pom.xml 和 myb-ddd-demo-infra/pom.xml 的 dependencies 加 mapstruct
- 两个pom加 maven-compiler-plugin 配置 annotationProcessorPaths（mapstruct-processor 在 lombok 前面）

## 任务2：SubmitApplyOrderDTO 补全字段（app/dto）
文件：my-ddd-demo-app/src/main/java/com/viw/ddd/demo/app/applyOrder/dto/SubmitApplyOrderDTO.java
- 字段和 SubmitApplyOrderCommand 一致：companyId, invoiceHeader, subject, applyAmount, freightFee, serviceFee, applyOrderDetailVOList(List<ApplyOrderDetailVO>), applyOrderExpressVO(ApplyOrderExpressVO)
- @Data，DDD注释：防腐层DTO，上游字段变更不影响业务逻辑

## 任务3：ApplyOrderAssembler（MapStruct Command→DTO）
新建：my-ddd-demo-app/src/main/java/com/viw/ddd/demo/app/applyOrder/assembler/ApplyOrderAssembler.java
- @Mapper(componentModel = "spring")
- SubmitApplyOrderDTO toDTO(SubmitApplyOrderCommand command);
- 加 INSTANCE 静态字段

## 任务4：CQRS 查询链路
### 4a. ApplyOrderQueryFacade 补充方法
文件：my-ddd-demo-api/.../ApplyOrderQueryFacade.java
- ApplyOrderQueryDTO findById(Long id);

### 4b. ApplyOrderQueryDTO（查询返回对象）
新建：my-ddd-demo-app/.../dto/ApplyOrderQueryDTO.java
- 字段：id, applyOrderNo, invoiceHeader, subject, applyDate, applyAmount, freightFee, serviceFee, totalAmount, status
- @Data @Builder @AllArgsConstructor @NoArgsConstructor

### 4c. ApplyOrderQueryService 接口 + 实现
接口：my-ddd-demo-app/.../service/ApplyOrderQueryService.java —— findById(Long id)
实现：my-ddd-demo-app/.../service/impl/ApplyOrderQueryServiceImpl.java
- @Service，构造函数注入 ApplyOrderRepository
- findById 调 repository → Entity → 手动转 QueryDTO

### 4d. ApplyOrderQueryFacadeImpl（Adapter层）
新建：my-ddd-demo-adapter/.../impl/ApplyOrderQueryFacadeImpl.java
- @Component，注入 ApplyOrderQueryService，转调

## 任务5：ApplyOrderDataAssembler（MapStruct DO↔Entity）
新建：myb-ddd-demo-infra/.../assembler/ApplyOrderDataAssembler.java
- @Mapper(componentModel = "spring")
- ApplyOrderDO toDO(ApplyOrderEntity entity);
- ApplyOrderEntity toEntity(ApplyOrderDO dataObject);

## 任务6：改造 ApplyOrderService 接口 + ApplyOrderServiceImpl
- 接口 submitApplyOrder 参数改为 SubmitApplyOrderDTO（之前是 SubmitApplyOrderCommand）
- Impl 对应修改参数类型，构造函数注入新增 ApplyOrderAssembler
- sendExpress 暂不改

## 任务7：改造 ApplyOrderRepositoryImpl
- 构造函数注入 ApplyOrderDataAssembler
- toDO() → applyOrderDataAssembler.toDO(entity);
- toEntity() → applyOrderDataAssembler.toEntity(doObj);
- 删除手写setter代码

## 任务8：改造 ApplyOrderCommandFacadeImpl（Adapter）
- 加 @Component，构造函数注入 ApplyOrderAssembler + ApplyOrderService
- submitApplyOrder 方法先 assembler.toDTO(command) → 再调 service.submitApplyOrder(dto)

## 任务9：编译验证
mvn clean compile -DskipTests 必须通过
