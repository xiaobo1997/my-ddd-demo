package com.viw.ddd.demo.domain.gateway;

import com.viw.ddd.demo.domain.company.CompanyDTO;

/**
 * 【DDD - 领域层（Domain）· 网关接口（Gateway Interface）—— 防腐层】
 *
 * Gateway（网关）是 DDD 中的防腐层（Anti-Corruption Layer）组件。
 * 
 * 为什么接口定义在领域层？
 * ┌─────────────────────────────────────────────────────┐
 * │ 领域层只需要知道"我需要什么外部数据"                 │
 * │ 不需要知道"外部系统怎么调用/数据结构长什么样"         │
 * │                                                     │
 * │ 领域层 ← 定义 Gateway 接口（契约）                   │
 * │ Infra层 ← 实现 Gateway 接口（Dubbo/Feign/HTTP）       │
 * └─────────────────────────────────────────────────────┘
 *
 * Gateway vs Repository 的区别：
 *   Repository → 操作本限界上下文的聚合根持久化（本数据库）
 *   Gateway    → 访问其他限界上下文/外部系统（跨系统调用）
 *
 * 面试要点：这是 DDD 依赖倒置的核心体现之一。
 *         "接口归领域层，实现归基础设施层"
 *
 * @author xhb
 */
public interface CompanyGateway {

    /**
     * 根据公司ID查询公司信息
     * 实际项目中：通过 Dubbo RPC / Feign / HTTP 调用公司服务
     *
     * @param companyId 公司ID
     * @return 公司信息 DTO
     */
    CompanyDTO findByCompanyId(Long companyId);
}
