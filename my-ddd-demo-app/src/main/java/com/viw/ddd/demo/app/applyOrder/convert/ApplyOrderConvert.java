package com.viw.ddd.demo.app.applyOrder.convert;

import com.viw.ddd.demo.app.applyOrder.event.publish.ApplyOrderSubmittedEvent;
import com.viw.ddd.demo.app.applyOrder.event.publish.ExpressSentEvent;
import com.viw.ddd.demo.domain.applyOrder.entity.ApplyOrderEntity;

/**
 * 【DDD - 应用层（Application）· 转换器接口（Convert Interface）】
 *
 * Convert 是 DDD 中负责对象转换的组件。
 * 典型场景：实体（Entity）→ 事件（Event）的数据转换。
 *
 * 与 Factory 的区别：
 *   Factory → 创建聚合根（Command → Entity）
 *   Convert → 数据格式转换（Entity → Event / DTO / VO 等）
 *
 * @author xhb
 */
public interface ApplyOrderConvert {

    /** 申请单实体 → 申请单提交事件 */
    ApplyOrderSubmittedEvent convertApplyOrderSubmittedEvent(ApplyOrderEntity applyOrderEntity);

    /** 申请单实体 → 快递发送事件 */
    ExpressSentEvent convertExpressSentEvent(ApplyOrderEntity applyOrderEntity);
}
