package com.viw.ddd.demo.app.applyOrder.event.publish;

import lombok.Builder;
import lombok.Data;

/**
 * @author xhb
 * @Date 2026/1/9
 * @Description :
 */
@Data
@Builder
public class ExpressSentEvent {
    private Long applyOrderId;
}
