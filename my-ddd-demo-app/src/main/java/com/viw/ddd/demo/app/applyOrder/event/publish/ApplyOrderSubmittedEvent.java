package com.viw.ddd.demo.app.applyOrder.event.publish;

import lombok.Data;

/**
 * 【DDD - 应用层（Application）· 领域事件（Domain Event）】
 *
 * 领域事件表示"已经发生的事情"，通常用于跨聚合的通知。
 * 命名：过去分词 + Event，如 ApplyOrderSubmittedEvent
 * 用途：发送到 MQ，让其他限界上下文（如财务系统、通知系统）消费
 *
 * @author xhb
 */
@Data
public class ApplyOrderSubmittedEvent {

    /** 已提交的申请单ID */
    private Long applyOrderId;
}
