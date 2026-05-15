package com.viw.ddd.demo.common.exception;

import lombok.Getter;

/**
 * 【DDD - 通用模块（Common）· 业务异常】
 *
 * 通用业务异常，所有业务层异常统一使用此类或其子类。
 * 配合 GlobalExceptionHandler 统一返回 Result.fail()。
 *
 * @author xhb
 */
@Getter
public class BusinessException extends RuntimeException {

    private final String code;
    private final String message;

    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BusinessException(String message) {
        this("BIZ_ERROR", message);
    }
}
