package com.viw.ddd.demo.app.applyOrder.convert;

import com.viw.ddd.demo.app.applyOrder.event.publish.ApplyOrderSubmittedEvent;
import com.viw.ddd.demo.app.applyOrder.event.publish.ExpressSentEvent;
import com.viw.ddd.demo.domin.applyOrder.entity.ApplyOrderEntity;

/**
 * @author xhb
 */
public class ApplyOrderConvertImpl implements ApplyOrderConvert {

    @Override
    public ApplyOrderSubmittedEvent convertApplyOrderSubmittedEvent(ApplyOrderEntity applyOrderEntity) {
        ApplyOrderSubmittedEvent event = new ApplyOrderSubmittedEvent();
        event.setApplyOrderId(applyOrderEntity.getId());
        return event;
    }

    @Override
    public ExpressSentEvent convertExpressSentEvent(ApplyOrderEntity applyOrderEntity) {
        return ExpressSentEvent.builder()
                .applyOrderId(applyOrderEntity.getId())
                .build();
    }
}
