package com.viw.ddd.demo.api.applyOrder.dto;

import com.viw.ddd.demo.domain.applyOrder.vo.ApplyOrderDetailVO;
import com.viw.ddd.demo.domain.applyOrder.vo.ApplyOrderExpressVO;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 【DDD - 接口层（API）· Command 命令对象】
 *
 * 提交申请单的命令对象。
 * 职责：封装一次"提交申请单"操作所需的全部输入参数。
 * 命名规范：业务动作 + Command，放在 api 模块的 dto 子包中。
 *
 * 在 DDD 中，Command 是一种显式的"意图表达"，
 * 区别于常见的 xxxRequest/xxxParam，它强调"我要做什么"而非"我要传什么数据"。
 *
 * @author xhb
 */
@Data
public class SubmitApplyOrderCommand implements Serializable {

    /** 客户公司ID — 用于通过 CompanyGateway 远程查询公司信息 */
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

    /** 申请单明细列表（值对象） */
    private List<ApplyOrderDetailVO> applyOrderDetailVOList;

    /** 快递信息（值对象） */
    private ApplyOrderExpressVO applyOrderExpressVO;
}
