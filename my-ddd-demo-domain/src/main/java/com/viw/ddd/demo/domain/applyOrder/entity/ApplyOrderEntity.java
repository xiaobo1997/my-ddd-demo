package com.viw.ddd.demo.domain.applyOrder.entity;

import com.viw.ddd.demo.domain.applyOrder.vo.ApplyOrderDetailVO;
import com.viw.ddd.demo.domain.applyOrder.vo.ApplyOrderExpressVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 领域模型申请单实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplyOrderEntity implements Serializable {

    // ========== 状态常量 ==========
    public static final String STATUS_DRAFT = "DRAFT";
    public static final String STATUS_APPROVED = "APPROVED";
    public static final String STATUS_BATCHING = "BATCHING";
    public static final String STATUS_INVOICED = "INVOICED";
    public static final String STATUS_MAILED = "MAILED";
    public static final String STATUS_EXPRESSED = "EXPRESSED";

    // ========== 字段 ==========
    private Long id;
    private Long companyId;
    private String applyOrderNo;
    private String invoiceHeader;
    private String subject;
    private Date applyDate;
    private BigDecimal applyAmount;
    private BigDecimal freightFee;
    private BigDecimal serviceFee;
    private BigDecimal totalAmount;
    private String status;

    private List<ApplyOrderDetailVO> applyOrderDetailVOList;
    private ApplyOrderExpressVO applyOrderExpressVO;

    // ========== 状态流转校验 ==========

    private void assertStatusIn(String... expected) {
        for (String s : expected) {
            if (s.equals(this.status)) {
                return;
            }
        }
        throw new IllegalStateException(
                "当前状态[" + status + "]不允许此操作，期望状态: " + String.join(",", expected));
    }

    // ========== 业务方法 ==========

    /**
     * 创建申请单 — 初始化状态、申请单号、申请日期
     */
    public void create() {
        this.applyOrderNo = generateOrderNo();
        this.applyDate = new Date();
        this.status = STATUS_DRAFT;
    }

    /**
     * 审批通过
     */
    public void approve() {
        assertStatusIn(STATUS_DRAFT);
        this.status = STATUS_APPROVED;
    }

    /**
     * 创建开票批次
     */
    public void createBatch() {
        assertStatusIn(STATUS_APPROVED);
        this.status = STATUS_BATCHING;
    }

    /**
     * 完成开票
     */
    public void finishInvoice() {
        assertStatusIn(STATUS_BATCHING);
        this.status = STATUS_INVOICED;
    }

    /**
     * 寄送发票（电子票）
     */
    public void sendMail() {
        assertStatusIn(STATUS_INVOICED);
        this.status = STATUS_MAILED;
    }

    /**
     * 快递纸质发票
     */
    public void sendExpress(String expressNo) {
        assertStatusIn(STATUS_MAILED);
        if (applyOrderExpressVO != null) {
            applyOrderExpressVO.send(expressNo);
        }
        this.status = STATUS_EXPRESSED;
    }

    // ========== 辅助方法 ==========

    /**
     * 生成申请单号：yyyyMMdd + 6位随机数
     */
    private String generateOrderNo() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd");
        String datePart = sdf.format(new Date());
        int random = (int) (Math.random() * 900000) + 100000;
        return "AP" + datePart + random;
    }
}
