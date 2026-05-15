package com.viw.ddd.demo.infra.util.mq.impl;

import com.viw.ddd.demo.infra.util.mq.MqSender;

/**
 * 【DDD - 基础设施层（Infrastructure）· MQ 发送器实现】
 *
 * MqSender 的实现。实际项目中：
 *   1. 注入 RocketMQ/Kafka 的 Producer
 *   2. 设置 Topic、Tag 等路由信息
 *   3. 发送消息
 *
 * @author xhb
 */
public class MqSenderImpl implements MqSender {

    @Override
    public void send(String msg) {
        // TODO: 注入 MQ 客户端，发送消息到指定 Topic
        // 示例：rocketMQProducer.send(message);
    }
}
