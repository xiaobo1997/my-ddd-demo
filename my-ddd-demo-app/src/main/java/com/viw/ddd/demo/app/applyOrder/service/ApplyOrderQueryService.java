package com.viw.ddd.demo.app.applyOrder.service;

import com.viw.ddd.demo.api.applyOrder.dto.ApplyOrderQueryDTO;

/**
 * 【DDD - 应用层（Application）· 查询服务接口（Query Service）—— CQRS Query 侧】
 *
 * 和 ApplyOrderService（命令服务）的区别：
 *   ApplyOrderService     → 写操作（涉及事务、领域事件）
 *   ApplyOrderQueryService → 读操作（只读、可跨聚合查询）
 *
 * 面试要点：
 *   "CQRS 把读写模型分开，查询服务不需要走领域模型，
 *    可以直接 SQL 查询，甚至走 ES 搜索引擎。"
 *
 * @author xhb
 */
public interface ApplyOrderQueryService {

    /**
     * 根据 ID 查询申请单
     * @param id 申请单ID
     * @return 查询结果 DTO，不存在返回 null
     */
    ApplyOrderQueryDTO findById(Long id);
}
