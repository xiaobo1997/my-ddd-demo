package com.viw.ddd.demo.infra.applyOrder.assembler;

import com.viw.ddd.demo.domain.applyOrder.vo.ApplyOrderDetailVO;
import com.viw.ddd.demo.infra.applyOrder.repository.dataobject.ApplyOrderDetailDO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * 【DDD - 基础设施层（Infrastructure）· MapStruct 明细映射器】
 *
 * 职责：DetailVO（领域值对象） ↔ DetailDO（数据对象）互相转换。
 *
 * 明细数据作为聚合根的一部分，在领域层以值对象形式表达，
 * 在持久层以独立的数据库行存储，Assembler 负责这两层模型的转换。
 *
 * @author xhb
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE  // DO 中的 applyOrderId 在 VO 中无对应字段，忽略
)
public interface ApplyOrderDetailAssembler {

    /** DetailVO → DetailDO（保存明细到数据库时） */
    ApplyOrderDetailDO toDO(ApplyOrderDetailVO detailVO);

    /** DetailDO → DetailVO（从数据库读取明细时） */
    ApplyOrderDetailVO toVO(ApplyOrderDetailDO dataObject);
}
