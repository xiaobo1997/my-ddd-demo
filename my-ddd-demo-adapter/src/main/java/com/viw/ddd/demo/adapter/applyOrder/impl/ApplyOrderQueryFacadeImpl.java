package com.viw.ddd.demo.adapter.applyOrder.impl;

import com.viw.ddd.demo.api.applyOrder.ApplyOrderQueryFacade;
import com.viw.ddd.demo.api.applyOrder.dto.ApplyOrderQueryDTO;
import com.viw.ddd.demo.app.applyOrder.service.ApplyOrderQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 【DDD - 适配层（Adapter）· 查询门面实现 —— CQRS Query 侧】
 *
 * 实现 api 层的 ApplyOrderQueryFacade，桥接外部查询请求到应用层查询服务。
 *
 * 和 ApplyOrderCommandFacadeImpl 的区别：
 *   Command Facade → 命令 + 防腐（Command → DTO 映射）
 *   Query Facade   → 纯转发（不需要防腐，查询本身就是读）
 *
 * @author xhb
 */
@Component
public class ApplyOrderQueryFacadeImpl implements ApplyOrderQueryFacade {

    private final ApplyOrderQueryService applyOrderQueryService;

    @Autowired
    public ApplyOrderQueryFacadeImpl(ApplyOrderQueryService applyOrderQueryService) {
        this.applyOrderQueryService = applyOrderQueryService;
    }

    @Override
    public ApplyOrderQueryDTO findById(Long id) {
        return applyOrderQueryService.findById(id);
    }
}
