package com.viw.ddd.demo.infra.company;

import com.viw.ddd.demo.infra.company.dto.CompanyDTO;

/**
 * 【DDD - 基础设施层（Infrastructure）· 外部网关接口（Gateway Interface）】
 *
 * Gateway（网关）是 DDD 中用于封装对外部系统调用的组件。
 * 典型场景：调用远程 RPC/HTTP 接口获取数据。
 * 
 * Gateway vs Repository 的区别：
 *   Repository → 操作本地数据库（当前限界上下文的数据）
 *   Gateway    → 访问外部系统（其他限界上下文的数据）
 *
 * @author xhb
 */
public interface CompanyGateway {

    /**
     * 根据公司ID查询公司信息
     * 实际项目中，这里会调用远程 RPC 接口
     */
    CompanyDTO findByCompanyId(Long companyId);
}
