package com.viw.ddd.demo.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 【DDD - 通用模块（Common）· 统一返回体】
 *
 * 所有 Controller 接口统一使用 Result<T> 包装返回。
 *
 * @param <T> 数据类型
 * @author xhb
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 状态码，"200" 表示成功 */
    private String code;
    /** 提示信息 */
    private String msg;
    /** 返回数据 */
    private T data;

    public static <T> Result<T> success(T data) {
        return new Result<>("200", "success", data);
    }

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> fail(String code, String msg) {
        return new Result<>(code, msg, null);
    }

    public static <T> Result<T> fail(String msg) {
        return fail("500", msg);
    }
}
