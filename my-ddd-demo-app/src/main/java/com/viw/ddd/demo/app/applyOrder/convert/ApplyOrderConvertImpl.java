package com.viw.ddd.demo.app.applyOrder.convert;

import com.viw.ddd.demo.app.applyOrder.event.publish.ApplyOrderSubmittedEvent;
import com.viw.ddd.demo.app.applyOrder.event.publish.ExpressSentEvent;
import com.viw.ddd.demo.domain.applyOrder.entity.ApplyOrderEntity;
import org.springframework.stereotype.Component;

/**
 * 【DDD - 应用层（Application）· 转换器实现】
 *
 * Convert 接口的实现。
 * 
 * @author xhb
 */
@Component
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
