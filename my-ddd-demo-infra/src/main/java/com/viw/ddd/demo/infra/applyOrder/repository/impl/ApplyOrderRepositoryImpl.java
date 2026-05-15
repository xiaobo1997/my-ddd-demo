package com.viw.ddd.demo.infra.applyOrder.repository.impl;

import com.viw.ddd.demo.domain.applyOrder.entity.ApplyOrderEntity;
import com.viw.ddd.demo.domain.applyOrder.repository.ApplyOrderRepository;
import com.viw.ddd.demo.domain.applyOrder.vo.ApplyOrderDetailVO;
import com.viw.ddd.demo.domain.applyOrder.vo.ApplyOrderExpressVO;
import com.viw.ddd.demo.infra.applyOrder.assembler.ApplyOrderDataAssembler;
import com.viw.ddd.demo.infra.applyOrder.assembler.ApplyOrderDetailAssembler;
import com.viw.ddd.demo.infra.applyOrder.assembler.ApplyOrderExpressAssembler;
import com.viw.ddd.demo.infra.applyOrder.repository.dataobject.ApplyOrderDO;
import com.viw.ddd.demo.infra.applyOrder.repository.dataobject.ApplyOrderDetailDO;
import com.viw.ddd.demo.infra.applyOrder.repository.dataobject.ApplyOrderExpressDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 【DDD - 基础设施层（Infrastructure）· 仓储实现 — 聚合根一致性持久化】
 *
 * DDD 核心原则：聚合是一个整体，存取必须保持一致性。
 *
 * 三张"表"：
 *   db         → ApplyOrderDO       （聚合根主表）
 *   detailDb   → ApplyOrderDetailDO （值对象：明细）
 *   expressDb  → ApplyOrderExpressDO（值对象：快递）
 *
 * 一致性保证：
 *   save：主DO + 明细 + 快递，全存
 *   findById：主DO + 明细 + 快递，全读，组装完整聚合
 *   update：先删旧子数据，再存新子数据（逻辑上替代物理删除）
 *
 * CRUD = 查询 + 修改，无物理删除方法（生产环境通过状态字段逻辑删除）。
 *
 * @author xhb
 */
@Repository
public class ApplyOrderRepositoryImpl implements ApplyOrderRepository {

    // ========== 三张内存表 ==========

    /** 聚合根主表 */
    private final Map<Long, ApplyOrderDO> db = new ConcurrentHashMap<>();
    /** 明细子表（一条申请单可有多条明细） */
    private final Map<Long, ApplyOrderDetailDO> detailDb = new ConcurrentHashMap<>();
    /** 快递子表（一条申请单只有一条快递信息） */
    private final Map<Long, ApplyOrderExpressDO> expressDb = new ConcurrentHashMap<>();

    // ========== ID 生成器 ==========

    private final AtomicLong idGenerator = new AtomicLong(1);
    private final AtomicLong detailIdGen = new AtomicLong(1);
    private final AtomicLong expressIdGen = new AtomicLong(1);

    // ========== MapStruct 映射器 ==========

    private final ApplyOrderDataAssembler dataAssembler;
    private final ApplyOrderDetailAssembler detailAssembler;
    private final ApplyOrderExpressAssembler expressAssembler;

    @Autowired
    public ApplyOrderRepositoryImpl(ApplyOrderDataAssembler dataAssembler,
                                     ApplyOrderDetailAssembler detailAssembler,
                                     ApplyOrderExpressAssembler expressAssembler) {
        this.dataAssembler = dataAssembler;
        this.detailAssembler = detailAssembler;
        this.expressAssembler = expressAssembler;
    }

    // ==================== save ====================

    @Override
    public Long save(ApplyOrderEntity entity) {
        if (entity.getId() == null) {
            entity.setId(idGenerator.getAndIncrement());
        }
        Long applyOrderId = entity.getId();

        // 1. 保存聚合根主表
        db.put(applyOrderId, dataAssembler.toDO(entity));

        // 2. 保存明细子表（值对象 → 数据对象，附加 applyOrderId 外键）
        List<ApplyOrderDetailVO> detailList = entity.getApplyOrderDetailVOList();
        if (detailList != null) {
            for (ApplyOrderDetailVO detailVO : detailList) {
                ApplyOrderDetailDO detailDO = detailAssembler.toDO(detailVO);
                if (detailDO.getId() == null) {
                    detailDO.setId(detailIdGen.getAndIncrement());
                }
                detailDO.setApplyOrderId(applyOrderId);
                detailDb.put(detailDO.getId(), detailDO);
            }
        }

        // 3. 保存快递子表
        ApplyOrderExpressVO expressVO = entity.getApplyOrderExpressVO();
        if (expressVO != null) {
            ApplyOrderExpressDO expressDO = expressAssembler.toDO(expressVO);
            if (expressDO.getId() == null) {
                expressDO.setId(expressIdGen.getAndIncrement());
            }
            expressDO.setApplyOrderId(applyOrderId);
            expressDb.put(expressDO.getId(), expressDO);
        }

        return applyOrderId;
    }

    // ==================== findById ====================

    @Override
    public ApplyOrderEntity findById(Long id) {
        ApplyOrderDO doObj = db.get(id);
        if (doObj == null) {
            return null;
        }

        // 1. 主表 → Entity
        ApplyOrderEntity entity = dataAssembler.toEntity(doObj);

        // 2. 明细子表 → VO 列表
        List<ApplyOrderDetailVO> detailList = detailDb.values().stream()
                .filter(d -> id.equals(d.getApplyOrderId()))
                .map(detailAssembler::toVO)
                .collect(Collectors.toList());
        if (!detailList.isEmpty()) {
            entity.setApplyOrderDetailVOList(detailList);
        }

        // 3. 快递子表 → VO（一条申请单只有一条快递，取第一个）
        ApplyOrderExpressVO expressVO = expressDb.values().stream()
                .filter(e -> id.equals(e.getApplyOrderId()))
                .map(expressAssembler::toVO)
                .findFirst()
                .orElse(null);
        entity.setApplyOrderExpressVO(expressVO);

        return entity;
    }

    // ==================== findById with type ====================

    @Override
    public ApplyOrderEntity findById(Long id, String type) {
        ApplyOrderEntity entity = findById(id);
        if (entity == null) {
            return null;
        }
        // "detail" 模式下只返回基础信息，过滤快递信息
        if ("detail".equals(type)) {
            entity.setApplyOrderExpressVO(null);
        }
        return entity;
    }

    // ==================== update（逻辑替代物理删除） ====================

    @Override
    public int update(ApplyOrderEntity oldApplyOrderEntity, ApplyOrderEntity newApplyOrderEntity) {
        Long applyOrderId = newApplyOrderEntity.getId();
        ApplyOrderDO exist = db.get(applyOrderId);
        if (exist == null) {
            return 0;
        }

        // 1. 更新主表
        db.put(applyOrderId, dataAssembler.toDO(newApplyOrderEntity));

        // 2. 删除旧的明细 + 快递（通过 ID 过滤删除，替代物理删除）
        List<Long> oldDetailIds = detailDb.values().stream()
                .filter(d -> applyOrderId.equals(d.getApplyOrderId()))
                .map(ApplyOrderDetailDO::getId)
                .collect(Collectors.toList());
        oldDetailIds.forEach(detailDb::remove);

        List<Long> oldExpressIds = expressDb.values().stream()
                .filter(e -> applyOrderId.equals(e.getApplyOrderId()))
                .map(ApplyOrderExpressDO::getId)
                .collect(Collectors.toList());
        oldExpressIds.forEach(expressDb::remove);

        // 3. 保存新的明细
        List<ApplyOrderDetailVO> detailList = newApplyOrderEntity.getApplyOrderDetailVOList();
        if (detailList != null) {
            for (ApplyOrderDetailVO detailVO : detailList) {
                ApplyOrderDetailDO detailDO = detailAssembler.toDO(detailVO);
                if (detailDO.getId() == null) {
                    detailDO.setId(detailIdGen.getAndIncrement());
                }
                detailDO.setApplyOrderId(applyOrderId);
                detailDb.put(detailDO.getId(), detailDO);
            }
        }

        // 4. 保存新的快递
        ApplyOrderExpressVO expressVO = newApplyOrderEntity.getApplyOrderExpressVO();
        if (expressVO != null) {
            ApplyOrderExpressDO expressDO = expressAssembler.toDO(expressVO);
            if (expressDO.getId() == null) {
                expressDO.setId(expressIdGen.getAndIncrement());
            }
            expressDO.setApplyOrderId(applyOrderId);
            expressDb.put(expressDO.getId(), expressDO);
        }

        return 1;
    }
}
