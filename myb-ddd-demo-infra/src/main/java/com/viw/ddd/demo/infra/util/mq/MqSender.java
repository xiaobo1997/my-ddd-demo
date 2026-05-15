package com.viw.ddd.demo.infra.util.mq;

/**
 * @author xhb
 * @Date 2026/1/9
 * @Description :
 */

public interface MqSender {

    void send(String msg);
}
