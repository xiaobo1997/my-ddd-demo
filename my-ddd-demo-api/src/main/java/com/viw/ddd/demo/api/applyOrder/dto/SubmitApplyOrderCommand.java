package com.viw.ddd.demo.api.applyOrder.dto;

import com.viw.ddd.demo.domain.applyOrder.vo.ApplyOrderDetailVO;
import com.viw.ddd.demo.domain.applyOrder.vo.ApplyOrderExpressVO;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author xhb
 * @Date 2026/1/8
 */
@Data
public class SubmitApplyOrderCommand implements Serializable {

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
