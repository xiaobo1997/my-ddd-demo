package com.viw.ddd.demo.common.exception;

import lombok.Getter;

/**
 * 【DDD - 通用模块（Common）· 参数校验异常】
 *
 * 用于 Controller 层参数校验失败时抛出，由 GlobalExceptionHandler 统一处理。
 *
 * @author xhb
 */
@Getter
public class ValidationException extends RuntimeException {

    private final String code;
    private final String message;

    public ValidationException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public ValidationException(String message) {
        this("VALIDATION_ERROR", message);
    }
}
