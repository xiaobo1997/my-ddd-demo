package com.viw.ddd.demo.api.applyOrder.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 【DDD - 接口层（API）· CQRS 查询结果 DTO】
 *
 * 查询返回的数据传输对象，属于 API 层的返回契约。
 * 和 Command DTO 一样定义在 api 层——外部调用方需要知道返回什么。
 *
 * 设计原则：
 *   1. 展平返回，不暴露领域对象内部结构（如 VO 嵌套）
 *   2. 只包含前端需要的字段
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
