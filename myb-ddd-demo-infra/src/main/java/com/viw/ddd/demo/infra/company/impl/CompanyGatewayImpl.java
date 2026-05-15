package com.viw.ddd.demo.infra.company.impl;

import com.viw.ddd.demo.infra.company.CompanyGateway;
import com.viw.ddd.demo.infra.company.dto.CompanyDTO;

/**
 * 【DDD - 基础设施层（Infrastructure）· 外部网关实现】
 *
 * CompanyGateway 的实现，模拟远程 RPC 调用。
 * 实际项目中，这里会通过 Dubbo/Feign 调用公司服务的接口。
 *
 * @author xhb
 */
public class CompanyGatewayImpl implements CompanyGateway {

    @Override
    public CompanyDTO findByCompanyId(Long companyId) {
        // TODO: 调用远程 RPC 接口查询公司信息
        // 当前返回空 DTO 作为骨架，实际需要调用公司服务的 Dubbo 接口
        return CompanyDTO.builder()
                .companyId(companyId)
                .build();
    }
}
