package com.viw.ddd.demo.app.applyOrder.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 【DDD - 应用层（Application）· CQRS 查询结果 DTO】
 *
 * 查询专用 DTO，和 Command DTO 的区别：
 *   Command DTO → 携带"要做什么"的参数（写）
 *   Query DTO   → 携带"查询结果"的数据（读）
 *
 * 设计原则：
 *   1. 展平返回，不暴露领域对象内部结构（如 VO 嵌套）
 *   2. 只包含前端需要的字段，不多不少
 *   3. 可以用 @Builder 方便构造
 *
 * @author xhb
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplyOrderQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String applyOrderNo;
    private String invoiceHeader;
    private String subject;
    private Date applyDate;
    private BigDecimal applyAmount;
    private BigDecimal freightFee;
    private BigDecimal serviceFee;
    private BigDecimal totalAmount;
    private String status;
}
