package com.viw.ddd.demo.domain.applyOrder.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 【DDD - 领域层（Domain）· 值对象（Value Object）】
 *
 * 值对象与实体的区别：
 *   实体（Entity）→ 有唯一标识（id），可变化，关注"是谁"
 *   值对象（VO）   → 无唯一标识，描述性，关注"是什么"
 *
 * ApplyOrderDetailVO 描述申请单中某一条明细的信息，没有独立 id，
 * 属于 ApplyOrderEntity 这个聚合根的一部分。
 *
 * @author xhb
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplyOrderDetailVO {

    /** 明细项ID（数据库主键，但不是领域层面的唯一标识） */
    private Long id;
    /** 发货地址 */
    private String deliveryAddress;
    /** 收货地址 */
    private String receivingAddress;
    /** 事由 */
    private String subject;
    /** 运单号 */
    private String orderNo;
    /** 创建时间 */
    private Date createTime;
    /** 完成时间 */
    private Date finishTime;
    /** 运费 */
    private BigDecimal freightFee;
    /** 运费税收编码 */
    private String freightFeeTaxCode;
    /** 运费税务号 */
    private String freightFeeTaxNo;
    /** 服务费 */
    private BigDecimal serviceFee;
    /** 服务费税收编码 */
    private String serviceFeeTaxCode;
    /** 服务费税务号 */
    private String serviceFeeTaxNo;
    /** 明细状态 */
    private String status;
}
