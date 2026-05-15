package com.viw.ddd.demo.api.applyOrder.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 【DDD - 接口层（API）· Command 命令对象】
 *
 * 发送快递的命令对象。
 * 封装"发送快递"操作所需的输入参数。
 *
 * @author xhb
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SendExpressCommand implements Serializable {

    /** 申请单ID — 标识要发送快递的申请单 */
    private Long applyOrderId;

    /** 快递单号 */
    private String expressNo;
}
