package com.viw.ddd.demo.app.applyOrder.dto;

import com.viw.ddd.demo.domain.applyOrder.vo.ApplyOrderDetailVO;
import com.viw.ddd.demo.domain.applyOrder.vo.ApplyOrderExpressVO;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 【DDD - 应用层（Application）· 防腐层 DTO（Anti-Corruption Layer DTO）】
 *
 * 应用层内部使用的数据传输对象。
 * 
 * 为什么需要这一层？
 *   api 层的 Command 是外部契约（来自 RPC/HTTP 调用方），
 *   直接传入 App 层会让外部变更（字段改名、类型变化）影响内部业务逻辑。
 *   加一层 DTO 作为防腐层：
 *     Command（外部） → [MapStruct 映射] → DTO（内部） → 业务逻辑
 *
 * 如果上游接口字段变更：
 *   ✅ 只需改 Command 和 Assembler 映射
 *   ✅ 业务逻辑代码完全不受影响
 *
 * 对应映射器：ApplyOrderAssembler（Command → DTO）
 *
 * @author xhb
 */
@Data
public class SubmitApplyOrderDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 客户公司ID */
    private Long companyId;

    /** 发票抬头 */
    private String invoiceHeader;

    /** 申请事由 */
    private String subject;

    /** 申请金额 */
    private BigDecimal applyAmount;

    /** 运费 */
    private BigDecimal freightFee;

    /** 服务费 */
    private BigDecimal serviceFee;

    /** 申请单明细列表 */
    private List<ApplyOrderDetailVO> applyOrderDetailVOList;

    /** 快递信息 */
    private ApplyOrderExpressVO applyOrderExpressVO;
}
