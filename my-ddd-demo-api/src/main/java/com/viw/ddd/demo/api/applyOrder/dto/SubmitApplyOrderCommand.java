package com.viw.ddd.demo.api.applyOrder.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author xhb
 * @Date 2026/1/8
 * @Description :
 */

@Data
public class SubmitApplyOrderCommand implements Serializable {

    private Long companyId;
}
