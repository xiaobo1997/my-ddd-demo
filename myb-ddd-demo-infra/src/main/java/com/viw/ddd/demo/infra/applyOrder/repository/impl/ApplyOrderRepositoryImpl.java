package com.viw.ddd.demo.infra.applyOrder.repository.impl;

import com.viw.ddd.demo.domain.applyOrder.entity.ApplyOrderEntity;
import com.viw.ddd.demo.domain.applyOrder.repository.ApplyOrderRepository;
import com.viw.ddd.demo.infra.applyOrder.repository.dataobject.ApplyOrderDO;
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
 * 完整实现需要：
 *   1. Entity → DO 转换（领域对象转持久化对象）
 *   2. DO → Entity 转换（持久化对象转领域对象）
 *   3. 数据库操作（当前为内存 Map）
 *
 * DO（Data Object）：
 *   与数据库表结构一一对应的对象，仅用于持久化层内部
 *   不能暴露给领域层，领域层只认识 Entity
 *
 * @author xhb
 */
@Repository
public class ApplyOrderRepositoryImpl implements ApplyOrderRepository {

    /** 内存数据库 */
    private final Map<Long, ApplyOrderDO> db = new ConcurrentHashMap<>();

    /** ID 生成器 */
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Long save(ApplyOrderEntity entity) {
        // 自动生成 ID（实际项目用 DB 自增或雪花算法）
        if (entity.getId() == null) {
            entity.setId(idGenerator.getAndIncrement());
        }
        ApplyOrderDO doObj = toDO(entity);
        db.put(entity.getId(), doObj);
        return entity.getId();
    }

    @Override
    public ApplyOrderEntity findById(Long id) {
        ApplyOrderDO doObj = db.get(id);
        if (doObj == null) {
            return null;
        }
        return toEntity(doObj);
    }

    @Override
    public ApplyOrderEntity findById(Long id, String type) {
        ApplyOrderEntity entity = findById(id);
        if (entity == null) {
            return null;
        }
        // "detail" 模式下只返回基础信息，不包含快递信息
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
        db.put(newApplyOrderEntity.getId(), toDO(newApplyOrderEntity));
        return 1;
    }

    // ========== DO ↔ Entity 转换 ==========

    /**
     * Entity → DO 转换
     * 职责：将领域对象转为持久化对象（写数据库时用）
     */
    private ApplyOrderDO toDO(ApplyOrderEntity entity) {
        if (entity == null) return null;
        ApplyOrderDO doObj = new ApplyOrderDO();
        doObj.setId(entity.getId());
        doObj.setApplyOrderNo(entity.getApplyOrderNo());
        doObj.setInvoiceHeader(entity.getInvoiceHeader());
        doObj.setSubject(entity.getSubject());
        doObj.setApplyDate(entity.getApplyDate());
        doObj.setApplyAmount(entity.getApplyAmount());
        doObj.setFreightFee(entity.getFreightFee());
        doObj.setServiceFee(entity.getServiceFee());
        doObj.setTotalAmount(entity.getTotalAmount());
        doObj.setStatus(entity.getStatus());
        return doObj;
    }

    /**
     * DO → Entity 转换
     * 职责：将持久化对象转为领域对象（读数据库后用）
     */
    private ApplyOrderEntity toEntity(ApplyOrderDO doObj) {
        if (doObj == null) return null;
        return ApplyOrderEntity.builder()
                .id(doObj.getId())
                .applyOrderNo(doObj.getApplyOrderNo())
                .invoiceHeader(doObj.getInvoiceHeader())
                .subject(doObj.getSubject())
                .applyDate(doObj.getApplyDate())
                .applyAmount(doObj.getApplyAmount())
                .freightFee(doObj.getFreightFee())
                .serviceFee(doObj.getServiceFee())
                .totalAmount(doObj.getTotalAmount())
                .status(doObj.getStatus())
                .build();
    }
}
