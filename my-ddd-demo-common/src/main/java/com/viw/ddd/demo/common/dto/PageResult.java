package com.viw.ddd.demo.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 【DDD - 通用模块（Common）· 分页结果】
 *
 * 分页查询统一返回体。
 *
 * @param <T> 数据类型
 * @author xhb
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 总记录数 */
    private long total;
    /** 总页数 */
    private long pages;
    /** 当前页数据 */
    private List<T> records;

    public static <T> PageResult<T> of(long total, long pages, List<T> records) {
        return new PageResult<>(total, pages, records);
    }
}
