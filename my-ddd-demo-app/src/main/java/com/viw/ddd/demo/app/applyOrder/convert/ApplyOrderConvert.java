package com.viw.ddd.demo.app.applyOrder.convert;

import com.viw.ddd.demo.app.applyOrder.event.publish.ApplyOrderSubmittedEvent;
import com.viw.ddd.demo.app.applyOrder.event.publish.ExpressSentEvent;
import com.viw.ddd.demo.domain.applyOrder.entity.ApplyOrderEntity;

/**
 * @author xhb
 * @Date 2026/1/8
 * @Description :
 */

public interface ApplyOrderConvert {

    ApplyOrderSubmittedEvent convertApplyOrderSubmittedEvent(ApplyOrderEntity  applyOrderEntity);


    ExpressSentEvent convertExpressSentEvent(ApplyOrderEntity applyOrderEntity);
}
