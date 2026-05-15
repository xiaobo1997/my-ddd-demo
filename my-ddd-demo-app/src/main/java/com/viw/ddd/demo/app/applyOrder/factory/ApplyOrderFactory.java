package com.viw.ddd.demo.app.applyOrder.factory;

import com.viw.ddd.demo.api.applyOrder.dto.SubmitApplyOrderCommand;
import com.viw.ddd.demo.domain.applyOrder.entity.ApplyOrderEntity;

import java.math.BigDecimal;

/**
 * @author xhb
 */
public class ApplyOrderFactory {

    /**
     * 从 Command 创建申请单实体
     */
    public static ApplyOrderEntity createApplyOrder(SubmitApplyOrderCommand command) {
        ApplyOrderEntity entity = ApplyOrderEntity.builder()
                .companyId(command.getCompanyId())
                .invoiceHeader(command.getInvoiceHeader())
                .subject(command.getSubject())
                .applyAmount(command.getApplyAmount())
                .freightFee(command.getFreightFee())
                .serviceFee(command.getServiceFee())
                .totalAmount(calculateTotal(command))
                .applyOrderDetailVOList(command.getApplyOrderDetailVOList())
                .applyOrderExpressVO(command.getApplyOrderExpressVO())
                .build();
        // 调用实体的初始化方法
        entity.create();
        return entity;
    }

    /**
     * 深拷贝实体（用于对比变更）
     */
    public static ApplyOrderEntity clone(ApplyOrderEntity source) {
        return ApplyOrderEntity.builder()
                .id(source.getId())
                .companyId(source.getCompanyId())
                .applyOrderNo(source.getApplyOrderNo())
                .invoiceHeader(source.getInvoiceHeader())
                .subject(source.getSubject())
                .applyDate(source.getApplyDate())
                .applyAmount(source.getApplyAmount())
                .freightFee(source.getFreightFee())
                .serviceFee(source.getServiceFee())
                .totalAmount(source.getTotalAmount())
                .status(source.getStatus())
                .applyOrderDetailVOList(source.getApplyOrderDetailVOList())
                .applyOrderExpressVO(source.getApplyOrderExpressVO())
                .build();
    }

    /**
     * 计算总金额 = 申请金额 + 运费 + 服务费
     */
    private static BigDecimal calculateTotal(SubmitApplyOrderCommand command) {
        BigDecimal amount = nullToZero(command.getApplyAmount());
        BigDecimal freight = nullToZero(command.getFreightFee());
        BigDecimal service = nullToZero(command.getServiceFee());
        return amount.add(freight).add(service);
    }

    private static BigDecimal nullToZero(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }
}
