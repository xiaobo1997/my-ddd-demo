package com.viw.ddd.demo.infra.util.mq;

/**
 * 【DDD - 基础设施层（Infrastructure）· MQ 发送器接口】
 *
 * 消息队列（MQ）发送器的抽象接口。
 * 用途：发送领域事件到其他系统。
 * 实现可以是 RocketMQ / Kafka / RabbitMQ 等。
 *
 * @author xhb
 */
public interface MqSender {

    /**
     * 发送消息
     * @param msg JSON 格式的消息内容
     */
    void send(String msg);
}
