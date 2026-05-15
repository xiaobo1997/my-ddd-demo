package com.viw.ddd.demo.api.applyOrder;

import com.viw.ddd.demo.api.applyOrder.dto.ApplyOrderQueryDTO;

/**
 * 【DDD - 接口层（API）· 查询门面（Query Facade）—— CQRS の Query 侧】
 *
 * CQRS（Command Query Responsibility Segregation）：
 *   命令（Command）= 写操作，查询（Query）= 读操作
 *   ApplyOrderCommandFacade 负责"写"，ApplyOrderQueryFacade 负责"读"
 *
 * 好处：
 *   1. 读写分离，各自独立演化
 *   2. 查询模型可专门优化（如直接返回 DTO 而非领域对象）
 *   3. 命令侧不需要关心"怎么查"，查询侧不需要关心"怎么写"
 *
 * @author xhb
 */
public interface ApplyOrderQueryFacade {

    /**
     * 根据 ID 查询申请单详情
     * @param id 申请单ID
     * @return 查询结果 DTO（展平后的视图，不包含领域细节）
     */
    ApplyOrderQueryDTO findById(Long id);
}
