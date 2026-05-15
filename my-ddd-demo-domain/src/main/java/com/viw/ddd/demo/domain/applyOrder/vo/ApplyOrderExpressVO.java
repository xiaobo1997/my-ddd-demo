package com.viw.ddd.demo.domain.applyOrder.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xhb
 * @Date 2026/1/9
 * @Description :
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplyOrderExpressVO {

    private Long id;
    private String recipient;
    private String contractPhone;
    private String deliveryAddress;
    private String deliveryCompany;
    private String expressNo;

    public void send(String expressNo) {
        //发送快递
        this.expressNo = expressNo;
    }
}
