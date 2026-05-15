package com.viw.ddd.demo.infra.company.impl;

import com.viw.ddd.demo.infra.company.CompanyGateway;
import com.viw.ddd.demo.infra.company.dto.CompanyDTO;

/**
 * @author xhb
 * @Date 2026/1/9
 * @Description :
 */

public class CompanyGatewayImpl implements CompanyGateway {

    @Override
    public CompanyDTO findByCompanyId(Long companyId) {
        //远程rpc接口， 查询 companyDTO
        //校验
        //返回
        return CompanyDTO.builder().build();
    }
}
