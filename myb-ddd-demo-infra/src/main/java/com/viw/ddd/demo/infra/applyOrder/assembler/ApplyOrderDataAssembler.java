package com.viw.ddd.demo.infra.applyOrder.assembler;

import com.viw.ddd.demo.domain.applyOrder.entity.ApplyOrderEntity;
import com.viw.ddd.demo.infra.applyOrder.repository.dataobject.ApplyOrderDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * 【DDD - 基础设施层（Infrastructure）· MapStruct 持久化映射器】
 *
 * 职责：Entity（领域对象） ↔ DO（数据对象）互相转换。
 *
 * 为什么用 MapStruct 替代手写转换？
 *   1. 字段多时手写 setter 容易漏、容易错
 *   2. 编译期生成代码，性能等同手写
 *   3. 新增字段时只需在 Entity/DO 加字段，MapStruct 自动映射
 *
 * 面试金句：
 *   "Entity ↔ DO 转换是 DDD 持久化防腐的关键一步。
 *    领域对象关注业务行为，DO 关注数据库结构，两者解耦后各自独立演化。"
 *
 * @author xhb
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE  // Entity 的 VO 字段在 DO 中没有对应，忽略
)
public interface ApplyOrderDataAssembler {

    ApplyOrderDataAssembler INSTANCE = Mappers.getMapper(ApplyOrderDataAssembler.class);

    /** Entity → DO（保存到数据库时） */
    ApplyOrderDO toDO(ApplyOrderEntity entity);

    /** DO → Entity（从数据库读取时） */
    @Mapping(target = "applyOrderDetailVOList", ignore = true)
    @Mapping(target = "applyOrderExpressVO", ignore = true)
    ApplyOrderEntity toEntity(ApplyOrderDO dataObject);
}
