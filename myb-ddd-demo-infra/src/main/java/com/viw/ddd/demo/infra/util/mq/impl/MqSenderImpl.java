package com.viw.ddd.demo.infra.util.mq.impl;

import com.viw.ddd.demo.domain.gateway.MqSender;
import org.springframework.stereotype.Component;

/**
 * 【DDD - 基础设施层（Infrastructure）· MQ 发送器实现】
 *
 * 实现领域层定义的 MqSender 接口。
 * 
 * 实际项目中：
 *   1. 注入 RocketMQ Producer / KafkaTemplate
 *   2. 设置 Topic、Tag 等路由信息
 *   3. 发送序列化后的消息
 *
 * DDD 分层：接口归 domain，实现留 infra。
 * 面试常问："为什么接口不放在实现同一层？"
 * 答案：依赖倒置——让领域层不依赖外部细节，外部细节反过来依赖领域抽象。
 *
 * @author xhb
 */
@Component
public class MqSenderImpl implements MqSender {

    @Override
    public void send(String msg) {
        // TODO: 注入 MQ 客户端，发送消息到指定 Topic
        // 示例：rocketMQTemplate.convertAndSend("apply-order-topic", msg);
    }
}
