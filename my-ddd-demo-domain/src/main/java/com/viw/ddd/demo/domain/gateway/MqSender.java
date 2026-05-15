package com.viw.ddd.demo.domain.gateway;

/**
 * 【DDD - 领域层（Domain）· 消息发送器接口（Gateway Interface）】
 *
 * 领域事件发布器，用于将领域事件发送到消息队列（MQ）。
 *
 * 为什么接口定义在领域层？
 *   领域层不关心底层是 RocketMQ / Kafka / RabbitMQ，
 *   只需要一个"send(msg)"的抽象。
 *   具体用哪种 MQ、怎么配置、怎么序列化——全部交给 infra 层。
 *
 * 这段话面试可以直接用：
 *   "领域层通过接口定义能力契约，基础设施层提供技术实现，
 *    这是 DDD 依赖倒置原则（DIP）的体现。"
 *
 * @author xhb
 */
public interface MqSender {

    /**
     * 发送消息到 MQ
     * @param msg JSON 格式的消息内容（领域事件序列化后）
     */
    void send(String msg);
}
