package com.viw.ddd.demo.app.applyOrder.service.impl;

import com.viw.ddd.demo.api.applyOrder.dto.ApplyOrderQueryDTO;
import com.viw.ddd.demo.app.applyOrder.service.ApplyOrderQueryService;
import com.viw.ddd.demo.domain.applyOrder.entity.ApplyOrderEntity;
import com.viw.ddd.demo.domain.applyOrder.repository.ApplyOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 【DDD - 应用层（Application）· 查询服务实现 —— CQRS Query 侧】
 *
 * 查询服务的特点：
 *   1. 只读操作，不需要事务
 *   2. 直接调 Repository 获取 Entity，转换为 QueryDTO 返回
 *   3. 不发布领域事件，不涉及状态变更
 *
 * 和 ApplyOrderServiceImpl（命令服务）的对比：
 *   Command Service → 编排 + 事务 + 事件 → 改状态
 *   Query Service   → 查 + 转 DTO → 不改任何东西
 *
 * @author xhb
 */
@Service
public class ApplyOrderQueryServiceImpl implements ApplyOrderQueryService {

    private final ApplyOrderRepository applyOrderRepository;

    @Autowired
    public ApplyOrderQueryServiceImpl(ApplyOrderRepository applyOrderRepository) {
        this.applyOrderRepository = applyOrderRepository;
    }

    @Override
    public ApplyOrderQueryDTO findById(Long id) {
        ApplyOrderEntity entity = applyOrderRepository.findById(id);
        if (entity == null) {
            return null;
        }
        // Entity → QueryDTO（展平，不返回嵌套 VO）
        return ApplyOrderQueryDTO.builder()
                .id(entity.getId())
                .applyOrderNo(entity.getApplyOrderNo())
                .invoiceHeader(entity.getInvoiceHeader())
                .subject(entity.getSubject())
                .applyDate(entity.getApplyDate())
                .applyAmount(entity.getApplyAmount())
                .freightFee(entity.getFreightFee())
                .serviceFee(entity.getServiceFee())
                .totalAmount(entity.getTotalAmount())
                .status(entity.getStatus().name())
                .build();
    }
}
