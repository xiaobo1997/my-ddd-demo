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
 * 【DDD - 领域层（Domain）· 聚合根（Aggregate Root）】
 *
 * 申请单（ApplyOrder）是整个申请单聚合的聚合根。
 * 聚合根是 DDD 中最重要的概念之一：
 *   - 它是外部访问聚合内实体的唯一入口
 *   - 它保证聚合内数据的一致性（所有状态变更需经过聚合根）
 *   - 它的生命周期贯穿整个业务过程
 *
 * 实体 vs 值对象：
 *   - 实体（Entity）：有唯一标识（id），可变化，用 id 区分不同对象
 *   - 值对象（Value Object）：无唯一标识，描述性，不可变，用属性值区分
 *   ApplyOrderEntity 是实体（有 id），ApplyOrderDetailVO 是值对象（无 id）
 *
 * 状态流转：
 *   DRAFT → APPROVED → BATCHING → INVOICED → MAILED → EXPRESSED
 *   每个方法内部有 assertStatusIn() 校验，防止非法跳转。
 *
 * @author xhb
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

    // ========== 实体属性 ==========
    /** 唯一标识（主键） */
    private Long id;
    /** 客户公司ID */
    private Long companyId;
    /** 申请单号（业务编号，非主键） */
    private String applyOrderNo;
    /** 发票抬头 */
    private String invoiceHeader;
    /** 申请事由 */
    private String subject;
    /** 申请日期 */
    private Date applyDate;
    /** 申请金额 */
    private BigDecimal applyAmount;
    /** 运费 */
    private BigDecimal freightFee;
    /** 服务费 */
    private BigDecimal serviceFee;
    /** 总金额 = 申请金额 + 运费 + 服务费 */
    private BigDecimal totalAmount;
    /** 状态 */
    private String status;

    // ========== 关联的值对象 ==========
    /** 申请单明细列表（值对象集合） */
    private List<ApplyOrderDetailVO> applyOrderDetailVOList;
    /** 快递信息（值对象） */
    private ApplyOrderExpressVO applyOrderExpressVO;

    // ========== 领域行为 ==========

    /**
     * 校验当前状态是否在期望状态集合中
     * DDD 实践中，将校验逻辑内聚在实体内部，避免贫血模型
     */
    private void assertStatusIn(String... expected) {
        for (String s : expected) {
            if (s.equals(this.status)) {
                return;
            }
        }
        throw new IllegalStateException(
                "当前状态[" + status + "]不允许此操作，期望状态: " + String.join(",", expected));
    }

    /** 创建申请单 — 初始化状态、申请单号、申请日期 */
    public void create() {
        this.applyOrderNo = generateOrderNo();
        this.applyDate = new Date();
        this.status = STATUS_DRAFT;
    }

    /** 审批通过 */
    public void approve() {
        assertStatusIn(STATUS_DRAFT);
        this.status = STATUS_APPROVED;
    }

    /** 创建开票批次 */
    public void createBatch() {
        assertStatusIn(STATUS_APPROVED);
        this.status = STATUS_BATCHING;
    }

    /** 完成开票 */
    public void finishInvoice() {
        assertStatusIn(STATUS_BATCHING);
        this.status = STATUS_INVOICED;
    }

    /** 寄送发票（电子票） */
    public void sendMail() {
        assertStatusIn(STATUS_INVOICED);
        this.status = STATUS_MAILED;
    }

    /** 快递纸质发票 — 调用值对象的 send() 方法 + 推进状态 */
    public void sendExpress(String expressNo) {
        assertStatusIn(STATUS_MAILED);
        if (applyOrderExpressVO != null) {
            applyOrderExpressVO.send(expressNo);
        }
        this.status = STATUS_EXPRESSED;
    }

    // ========== 辅助方法 ==========

    /**
     * 生成申请单号：AP + yyyyMMdd + 6位随机数
     * 实际项目中，申请单号通常由编号生成服务统一生成
     */
    private String generateOrderNo() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd");
        String datePart = sdf.format(new Date());
        int random = (int) (Math.random() * 900000) + 100000;
        return "AP" + datePart + random;
    }
}
