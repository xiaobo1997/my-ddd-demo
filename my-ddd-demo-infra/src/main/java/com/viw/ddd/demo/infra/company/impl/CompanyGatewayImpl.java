package com.viw.ddd.demo.infra.company.impl;

import com.viw.ddd.demo.domain.company.CompanyDTO;
import com.viw.ddd.demo.domain.gateway.CompanyGateway;
import org.springframework.stereotype.Component;

/**
 * 【DDD - 基础设施层（Infrastructure）· 外部网关实现】
 *
 * 实现领域层定义的 CompanyGateway 接口。
 * 
 * 实际项目中：
 *   1. 注入 Dubbo 的 @Reference 或 Feign 客户端
 *   2. 调用远程公司服务的 RPC 接口
 *   3. 将返回的外部 DO 转换为领域层的 CompanyDTO 返回
 *
 * DDD 分层关键：
 *   接口定义在 domain 层（com.viw.ddd.demo.domain.gateway）
 *   实现类留在 infra 层（com.viw.ddd.demo.infra.company.impl）
 *   这是"依赖倒置"——infra 依赖 domain，而不是反过来
 *
 * @author xhb
 */
@Component
public class CompanyGatewayImpl implements CompanyGateway {

    @Override
    public CompanyDTO findByCompanyId(Long companyId) {
        // TODO: 调用远程 RPC 接口查询公司信息
        // 示例：CompanyRpcDTO dto = companyRpcService.queryById(companyId);
        //       return CompanyDTO.builder().companyId(dto.getId()).companyName(dto.getName()).build();
        return CompanyDTO.builder()
                .companyId(companyId)
                .build();
    }
}
