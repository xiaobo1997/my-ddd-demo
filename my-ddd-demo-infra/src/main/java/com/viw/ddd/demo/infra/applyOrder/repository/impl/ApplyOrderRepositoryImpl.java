package com.viw.ddd.demo.infra.applyOrder.repository.impl;

import com.viw.ddd.demo.domain.applyOrder.entity.ApplyOrderEntity;
import com.viw.ddd.demo.domain.applyOrder.repository.ApplyOrderRepository;
import com.viw.ddd.demo.infra.applyOrder.assembler.ApplyOrderDataAssembler;
import com.viw.ddd.demo.infra.applyOrder.repository.dataobject.ApplyOrderDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 【DDD - 基础设施层（Infrastructure）· 仓储实现（Repository Implementation）】
 *
 * 仓储接口的持久化实现。
 * 当前用 ConcurrentHashMap 模拟数据库，体现了 DDD 的一个重要思想：
 *   领域层定义"做什么"（接口），基础设施层定义"怎么做"（实现）。
 *
 * Entity ↔ DO 转换：
 *   通过 ApplyOrderDataAssembler（MapStruct）完成，
 *   替代手写 setter/getter，新增字段时自动映射。
 *
 * @author xhb
 */
@Repository
public class ApplyOrderRepositoryImpl implements ApplyOrderRepository {

    /** 内存数据库 */
    private final Map<Long, ApplyOrderDO> db = new ConcurrentHashMap<>();

    /** ID 生成器 */
    private final AtomicLong idGenerator = new AtomicLong(1);

    /** MapStruct Entity↔DO 映射器 */
    private final ApplyOrderDataAssembler dataAssembler;

    @Autowired
    public ApplyOrderRepositoryImpl(ApplyOrderDataAssembler dataAssembler) {
        this.dataAssembler = dataAssembler;
    }

    @Override
    public Long save(ApplyOrderEntity entity) {
        if (entity.getId() == null) {
            entity.setId(idGenerator.getAndIncrement());
        }
        ApplyOrderDO doObj = dataAssembler.toDO(entity);
        db.put(entity.getId(), doObj);
        return entity.getId();
    }

    @Override
    public ApplyOrderEntity findById(Long id) {
        ApplyOrderDO doObj = db.get(id);
        if (doObj == null) {
            return null;
        }
        return dataAssembler.toEntity(doObj);
    }

    @Override
    public ApplyOrderEntity findById(Long id, String type) {
        ApplyOrderEntity entity = findById(id);
        if (entity == null) {
            return null;
        }
        if ("detail".equals(type)) {
            entity.setApplyOrderExpressVO(null);
        }
        return entity;
    }

    @Override
    public int update(ApplyOrderEntity oldApplyOrderEntity, ApplyOrderEntity newApplyOrderEntity) {
        ApplyOrderDO exist = db.get(newApplyOrderEntity.getId());
        if (exist == null) {
            return 0;
        }
        db.put(newApplyOrderEntity.getId(), dataAssembler.toDO(newApplyOrderEntity));
        return 1;
    }
}
