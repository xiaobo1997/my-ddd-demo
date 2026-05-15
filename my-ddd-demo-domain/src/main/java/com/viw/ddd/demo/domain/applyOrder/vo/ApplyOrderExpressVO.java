package com.viw.ddd.demo.domain.applyOrder.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 【DDD - 领域层（Domain）· 值对象（Value Object）】
 *
 * 值对象（VO）是 DDD 中用来描述某个属性的对象。
 * 特点：无唯一标识、通常不可变、通过属性值区分是否相等。
 *
 * ApplyOrderExpressVO 描述快递信息，作为 ApplyOrderEntity 的一部分存在。
 * 它的生命周期依附于聚合根，不会单独被查询或修改。
 *
 * @author xhb
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplyOrderExpressVO {

    private Long id;
    /** 收件人 */
    private String recipient;
    /** 联系电话 */
    private String contractPhone;
    /** 收件地址 */
    private String deliveryAddress;
    /** 快递公司 */
    private String deliveryCompany;
    /** 快递单号 */
    private String expressNo;

    /**
     * 发送快递 — 值对象内部的行为
     * DDD 中值对象也可以有自己的行为，只要不改变自身的不可变性
     */
    public void send(String expressNo) {
        this.expressNo = expressNo;
    }
}
