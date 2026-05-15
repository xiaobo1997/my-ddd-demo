package com.viw.ddd.demo.api.applyOrder.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author xhb
 * @Date 2026/1/8
 * @Description :
 */


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SendExpressCommand implements Serializable {

    private Long applyOrderId;

    private String expressNo;
}
