package com.viw.ddd.demo.infra.applyOrder.repository.do;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 申请单明细数据对象
 */
public class ApplyOrderDetailDO {

    private Long id;
    private Long applyOrderId;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getApplyOrderId() {
        return applyOrderId;
    }

    public void setApplyOrderId(Long applyOrderId) {
        this.applyOrderId = applyOrderId;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getReceivingAddress() {
        return receivingAddress;
    }

    public void setReceivingAddress(String receivingAddress) {
        this.receivingAddress = receivingAddress;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public BigDecimal getFreightFee() {
        return freightFee;
    }

    public void setFreightFee(BigDecimal freightFee) {
        this.freightFee = freightFee;
    }

    public String getFreightFeeTaxCode() {
        return freightFeeTaxCode;
    }

    public void setFreightFeeTaxCode(String freightFeeTaxCode) {
        this.freightFeeTaxCode = freightFeeTaxCode;
    }

    public String getFreightFeeTaxNo() {
        return freightFeeTaxNo;
    }

    public void setFreightFeeTaxNo(String freightFeeTaxNo) {
        this.freightFeeTaxNo = freightFeeTaxNo;
    }

    public BigDecimal getServiceFee() {
        return serviceFee;
    }

    public void setServiceFee(BigDecimal serviceFee) {
        this.serviceFee = serviceFee;
    }

    public String getServiceFeeTaxCode() {
        return serviceFeeTaxCode;
    }

    public void setServiceFeeTaxCode(String serviceFeeTaxCode) {
        this.serviceFeeTaxCode = serviceFeeTaxCode;
    }

    public String getServiceFeeTaxNo() {
        return serviceFeeTaxNo;
    }

    public void setServiceFeeTaxNo(String serviceFeeTaxNo) {
        this.serviceFeeTaxNo = serviceFeeTaxNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
