package com.viw.ddd.demo.domin.applyOrder.entity;

import com.viw.ddd.demo.domin.applyOrder.vo.ApplyOrderDetailVO;
import com.viw.ddd.demo.domin.applyOrder.vo.ApplyOrderExpressVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author xhb
 * @Date 2026/1/9
 * @Description :
 */

/**
 * 领域模型申请单实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplyOrderEntity implements Serializable {

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

    /**
     * 值对象开始
     */
    private List<ApplyOrderDetailVO> applyOrderDetailVOList;
    private ApplyOrderExpressVO applyOrderExpressVO;


    /**
     * 创建申请单
     */
    public void create(){

    }


    /**
     * 审批申请单
     */
    public void approve(){

    }

    /**
     * 创建批次
     */
    public void createBatch(){

    }

    /**
     * 完成开票
     */
    public void finishInvoice(){

    }

    /**
     * 发生数票
     */
    public void sendMail(){

    }

    /**
     * 快递纸制发票
     */
    public void sendExpress(String expressNo){
        applyOrderExpressVO.sned(expressNo);
        //已发送
        status = "EXPRESSED";
    }




}
