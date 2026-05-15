package com.viw.ddd.demo.infra.applyOrder.assembler;

import com.viw.ddd.demo.domain.applyOrder.vo.ApplyOrderExpressVO;
import com.viw.ddd.demo.infra.applyOrder.repository.dataobject.ApplyOrderExpressDO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * 【DDD - 基础设施层（Infrastructure）· MapStruct 快递映射器】
 *
 * 职责：ExpressVO（领域值对象） ↔ ExpressDO（数据对象）互相转换。
 *
 * 快递信息作为聚合根的一部分，在领域层以值对象形式表达，
 * 在持久层以独立的数据库行存储，Assembler 负责这两层模型的转换。
 *
 * @author xhb
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE  // DO 中的 applyOrderId 在 VO 中无对应字段，忽略
)
public interface ApplyOrderExpressAssembler {

    /** ExpressVO → ExpressDO（保存快递信息到数据库时） */
    ApplyOrderExpressDO toDO(ApplyOrderExpressVO expressVO);

    /** ExpressDO → ExpressVO（从数据库读取快递信息时） */
    ApplyOrderExpressVO toVO(ApplyOrderExpressDO dataObject);
}
