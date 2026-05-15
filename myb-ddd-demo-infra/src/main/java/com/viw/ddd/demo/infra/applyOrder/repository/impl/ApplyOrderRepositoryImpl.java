package com.viw.ddd.demo.infra.applyOrder.repository.impl;

import com.viw.ddd.demo.domain.applyOrder.entity.ApplyOrderEntity;
import com.viw.ddd.demo.domain.applyOrder.repository.ApplyOrderRepository;
import com.viw.ddd.demo.domain.applyOrder.vo.ApplyOrderDetailVO;
import com.viw.ddd.demo.domain.applyOrder.vo.ApplyOrderExpressVO;
import com.viw.ddd.demo.infra.applyOrder.repository.do.ApplyOrderDO;
import com.viw.ddd.demo.infra.applyOrder.repository.do.ApplyOrderDetailDO;
import com.viw.ddd.demo.infra.applyOrder.repository.do.ApplyOrderExpressDO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 申请单仓储实现（内存版）
 */
public class ApplyOrderRepositoryImpl implements ApplyOrderRepository {

    /** 内存数据库 */
    private final Map<Long, ApplyOrderDO> db = new ConcurrentHashMap<>();

    /** ID 生成器 */
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Long save(ApplyOrderEntity entity) {
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
