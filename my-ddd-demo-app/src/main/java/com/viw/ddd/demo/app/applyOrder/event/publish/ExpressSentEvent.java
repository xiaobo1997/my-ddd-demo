package com.viw.ddd.demo.app.applyOrder.event.publish;

import lombok.Builder;
import lombok.Data;

/**
 * 【DDD - 应用层（Application）· 领域事件（Domain Event）】
 *
 * 快递发送事件，在实体调用 sendExpress() 后由应用服务发布。
 *
 * @author xhb
 */
@Data
@Builder
public class ExpressSentEvent {

    /** 发生快递的申请单ID */
    private Long applyOrderId;
}
