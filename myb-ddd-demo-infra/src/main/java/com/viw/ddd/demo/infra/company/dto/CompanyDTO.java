package com.viw.ddd.demo.infra.company.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author xhb
 * @Date 2026/1/9
 * @Description :
 */


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompanyDTO implements Serializable {

    private Long companyId;

}
