package com.viw.ddd.demo.infra.applyOrder.repository.do;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 【DDD - 基础设施层（Infrastructure）· 数据对象（Data Object）】
 *
 * DO（Data Object）是与数据库表结构一一对应的持久化对象。
 * 
 * 三层对象的区别：
 *   Entity（领域实体）   → 包含业务行为的领域对象
 *   DO（数据对象）       → 与数据库表字段一一对应，纯数据，无行为
 *   VO（值对象/视图对象）→ 领域中的描述性对象 or Controller 返回的视图对象
 *
 * 注意：DO 只在基础设施层内部使用，不对外暴露。
 *
 * @author xhb
 */
public class ApplyOrderDO {

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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getApplyOrderNo() { return applyOrderNo; }
    public void setApplyOrderNo(String applyOrderNo) { this.applyOrderNo = applyOrderNo; }

    public String getInvoiceHeader() { return invoiceHeader; }
    public void setInvoiceHeader(String invoiceHeader) { this.invoiceHeader = invoiceHeader; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public Date getApplyDate() { return applyDate; }
    public void setApplyDate(Date applyDate) { this.applyDate = applyDate; }

    public BigDecimal getApplyAmount() { return applyAmount; }
    public void setApplyAmount(BigDecimal applyAmount) { this.applyAmount = applyAmount; }

    public BigDecimal getFreightFee() { return freightFee; }
    public void setFreightFee(BigDecimal freightFee) { this.freightFee = freightFee; }

    public BigDecimal getServiceFee() { return serviceFee; }
    public void setServiceFee(BigDecimal serviceFee) { this.serviceFee = serviceFee; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
