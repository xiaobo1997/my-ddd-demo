package com.viw.ddd.demo.app.applyOrder.factory;

import com.alibaba.fastjson.JSONObject;
import com.viw.ddd.demo.api.applyOrder.dto.SubmitApplyOrderCommand;
import com.viw.ddd.demo.domin.applyOrder.entity.ApplyOrderEntity;

/**
 * @author xhb
 * @Date 2026/1/8
 * @Description :
 */

public class ApplyOrderFactory {

    public static ApplyOrderEntity createApplyOrder(SubmitApplyOrderCommand command) {
        return ApplyOrderEntity.builder().build();
    }

    public static ApplyOrderEntity clone(ApplyOrderEntity applyOrderEntity) {
        return JSONObject.parseObject(JSONObject.toJSONString(applyOrderEntity), ApplyOrderEntity.class);
    }
}
