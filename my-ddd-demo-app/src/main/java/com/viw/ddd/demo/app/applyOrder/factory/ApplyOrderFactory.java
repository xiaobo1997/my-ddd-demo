package com.viw.ddd.demo.app.applyOrder.factory;

import com.viw.ddd.demo.api.applyOrder.dto.SubmitApplyOrderCommand;
import com.viw.ddd.demo.domain.applyOrder.entity.ApplyOrderEntity;

import java.math.BigDecimal;

/**
 * 【DDD - 应用层（Application）· 工厂（Factory）】
 *
 * 工厂模式在 DDD 中的作用：
 *   1. 封装复杂对象的创建逻辑
 *   2. 将"数据"转换为"领域对象"（从 Command/DO 转成 Entity）
 *   3. 通常以静态方法的形式提供服务
 *
 * 工厂 vs 转换器（Convert）的区别：
 *   Factory → 创建聚合根（从无到有）
 *   Convert → 转换数据格式（从 A 形态到 B 形态，如 Entity → Event）
 *
 * @author xhb
 */
public class ApplyOrderFactory {

    /**
     * 从 Command 创建申请单实体
     * 流程：接收 Command 数据 → builder 填充 → 调用实体的 create() 初始化
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
        // 调用实体的 create() 初始化状态、申请单号、申请日期
        entity.create();
        return entity;
    }

    /**
     * 深拷贝实体（快照模式）
     * 用于更新前保存旧状态，方便后续对比变更
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
