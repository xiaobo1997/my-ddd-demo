package com.viw.ddd.demo.common.enums;

import lombok.Getter;

/**
 * 【DDD - 通用模块（Common）· 申请单状态枚举】
 *
 * 状态流转：
 *   DRAFT → EXPRESSED → APPROVED → CREATE_BATCH → INVOICE_FINISHED → SENT_MAIL → SENT_EXPRESS
 *
 * @author xhb
 */
@Getter
public enum ApplyOrderStatusEnum {

    DRAFT("DRAFT", "草稿"),
    EXPRESSED("EXPRESSED", "已表达"),
    APPROVED("APPROVED", "已审批"),
    CREATE_BATCH("CREATE_BATCH", "已创建批次"),
    INVOICE_FINISHED("INVOICE_FINISHED", "已完成开票"),
    SENT_MAIL("SENT_MAIL", "已发送邮件"),
    SENT_EXPRESS("SENT_EXPRESS", "已发送快递");

    private final String code;
    private final String desc;

    ApplyOrderStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
