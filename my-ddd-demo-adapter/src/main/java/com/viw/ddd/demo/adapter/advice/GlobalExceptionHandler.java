package com.viw.ddd.demo.adapter.advice;

import com.viw.ddd.demo.common.dto.Result;
import com.viw.ddd.demo.common.exception.BusinessException;
import com.viw.ddd.demo.common.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 【DDD - 适配层（Adapter）· 全局异常处理器】
 *
 * DDD 分层视角下的异常处理定位：
 *   异常处理属于"外部适配"——将内部异常翻译为 HTTP 响应，是适配层的职责。
 *   领域层抛出的业务异常（BusinessException）在此统一捕获并转换为 Result 返回给外部。
 *
 * @author xhb
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** 业务异常 → Result.fail() */
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        log.warn("业务异常: code={}, msg={}", e.getCode(), e.getMessage());
        return Result.fail(e.getCode(), e.getMessage());
    }

    /** 参数校验异常 → Result.fail() */
    @ExceptionHandler(ValidationException.class)
    public Result<?> handleValidationException(ValidationException e) {
        log.warn("参数校验异常: code={}, msg={}", e.getCode(), e.getMessage());
        return Result.fail(e.getCode(), e.getMessage());
    }

    /** 兜底异常处理 */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.fail("SYSTEM_ERROR", "系统内部异常，请联系管理员");
    }
}
