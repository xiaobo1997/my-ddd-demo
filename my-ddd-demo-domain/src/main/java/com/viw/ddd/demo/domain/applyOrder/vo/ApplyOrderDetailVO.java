package com.viw.ddd.demo.domain.applyOrder.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author xhb
 * @Date 2026/1/9
 * @Description :
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplyOrderDetailVO {

    private Long id;
    private String deliveryAddress;
    private String receivingAddress;
    private String subject;
    private String orderNo;
    private Date createTime;
    private Date finishTime;
    private BigDecimal freightFee;
    private String freightFeeTaxCode;
    private String freightFeeTaxNo;
    private BigDecimal serviceFee;
    private String serviceFeeTaxCode;
    private String serviceFeeTaxNo;
    private String status;

}
