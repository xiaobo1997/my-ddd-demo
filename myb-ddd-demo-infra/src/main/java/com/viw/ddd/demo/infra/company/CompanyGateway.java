package com.viw.ddd.demo.infra.company;

import com.viw.ddd.demo.infra.company.dto.CompanyDTO;

/**
 * @author xhb
 * @Date 2026/1/9
 * @Description :
 */

public interface CompanyGateway {

    CompanyDTO findByCompanyId(Long companyId);
}
