package com.viw.ddd.demo.adapter.consumer;

import com.alibaba.fastjson.JSONObject;
import com.viw.ddd.demo.app.applyOrder.dto.SendExpressDTO;
import com.viw.ddd.demo.app.applyOrder.service.ApplyOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 【DDD - 适配层（Adapter）· MQ 消费者】
 *
 * DDD 分层视角下的 MQ 消费者定位：
 *   MQ 消费者是"外部消息驱动"的入口，和 Controller、定时任务一样属于适配层。
 *   它接收外部消息 → 反序列化 → 调用应用层服务，不直接操作领域对象。
 *
 * 实际项目中：
 *   - 使用 @RabbitListener 或 @KafkaListener 声明监听
 *   - 消费逻辑加幂等校验（根据 messageId 防重）
 *   - 失败后进入死信队列
 *
 * @author xhb
 */
@Component
public class ApplyOrderEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(ApplyOrderEventConsumer.class);

    private final ApplyOrderService applyOrderService;

    @Autowired
    public ApplyOrderEventConsumer(ApplyOrderService applyOrderService) {
        this.applyOrderService = applyOrderService;
    }

    /**
     * 模拟 MQ 消息消费：监听"申请单已审批"事件，自动触发快递寄送
     *
     * 生产代码示例：
     * <pre>
     * @KafkaListener(topics = "apply-order-approved", groupId = "apply-order-group")
     * public void onMessage(String messageJson) {
     *     // 1. 反序列化
     *     // 2. 去重校验
     *     // 3. 委托给应用层
     * }
     * </pre>
     *
     * @param messageJson MQ 消息 JSON 字符串
     */
    public void onMessage(String messageJson) {
        log.info("[MQ消费者] 收到消息: {}", messageJson);

        JSONObject msg = JSONObject.parseObject(messageJson);
        Long applyOrderId = msg.getLong("applyOrderId");

        // 反序列化后委托给应用层处理
        SendExpressDTO dto = new SendExpressDTO();
        dto.setApplyOrderId(applyOrderId);
        applyOrderService.sendExpress(dto);

        log.info("[MQ消费者] 消息处理完成: applyOrderId={}", applyOrderId);
    }
}
