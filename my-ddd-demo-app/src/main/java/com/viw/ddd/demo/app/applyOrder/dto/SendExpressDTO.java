package com.viw.ddd.demo.app.applyOrder.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 【DDD - 应用层（Application）· 防腐层 DTO — 发送快递】
 *
 * api 层的 SendExpressCommand 是外部契约，
 * 加一层 DTO 防止外部变更影响内部业务逻辑。
 *
 * @author xhb
 */
@Data
public class SendExpressDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 申请单ID */
    private Long applyOrderId;

    /** 快递单号 */
    private String expressNo;
}
