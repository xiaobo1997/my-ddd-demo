package com.viw.ddd.demo.domain.applyOrder.repository;

import com.viw.ddd.demo.domain.applyOrder.entity.ApplyOrderEntity;

/**
 * 【DDD - 领域层（Domain）· 仓储接口（Repository Interface）】
 *
 * 仓储（Repository）是 DDD 中聚合根的持久化抽象。
 * 作用：
 *   1. 将聚合根的持久化逻辑与领域逻辑解耦
 *   2. 领域层只定义接口，实现放基础设施层（infra）
 *   3. 领域服务通过仓储接口操作聚合根，不关心底层是 MySQL/Redis/内存
 *
 * 命名规范：聚合根名称 + Repository
 * 方法命名：save / findById / update 等标准操作
 *
 * @author xhb
 */
public interface ApplyOrderRepository {

    /** 保存申请单（新增） */
    Long save(ApplyOrderEntity applyOrderEntity);

    /** 根据ID查询申请单 */
    ApplyOrderEntity findById(Long id);

    /**
     * 根据ID查询申请单，按类型控制查询范围
     * @param type "detail" 只查询明细、null或空查询全部
     */
    ApplyOrderEntity findById(Long id, String type);

    /**
     * 更新申请单（乐观锁模式）
     * @param oldApplyOrderEntity 更新前的实体（用于对比变更）
     * @param newApplyOrderEntity 更新后的实体
     * @return 影响行数
     */
    int update(ApplyOrderEntity oldApplyOrderEntity, ApplyOrderEntity newApplyOrderEntity);
}
