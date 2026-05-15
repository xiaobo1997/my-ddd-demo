package com.viw.ddd.demo.app.applyOrder.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.viw.ddd.demo.api.applyOrder.dto.SendExpressCommand;
import com.viw.ddd.demo.app.applyOrder.convert.ApplyOrderConvert;
import com.viw.ddd.demo.app.applyOrder.dto.SubmitApplyOrderDTO;
import com.viw.ddd.demo.app.applyOrder.factory.ApplyOrderFactory;
import com.viw.ddd.demo.app.applyOrder.service.ApplyOrderService;
import com.viw.ddd.demo.domain.applyOrder.entity.ApplyOrderEntity;
import com.viw.ddd.demo.domain.applyOrder.repository.ApplyOrderRepository;
import com.viw.ddd.demo.domain.company.CompanyDTO;
import com.viw.ddd.demo.domain.gateway.CompanyGateway;
import com.viw.ddd.demo.domain.gateway.MqSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 【DDD - 应用层（Application）· 应用服务实现】
 *
 * 应用服务是典型的"编排"角色：
 *   1. submitApplyOrder: 查询公司 → 构造实体 → 保存 → 发事件
 *   2. sendExpress:    查询实体 → 快照(old) → 执行业务 → 对比更新 → 发事件
 *
 * 防腐设计：
 *   接收的是 app 层 DTO（已由适配层的 Assembler 从 api Command 转换过来）
 *   不直接依赖 api 层的 Command，上游字段变更不影响本方法
 *
 * @author xhb
 */
@Service
public class ApplyOrderServiceImpl implements ApplyOrderService {

    private final ApplyOrderRepository applyOrderRepository;
    private final CompanyGateway companyGateway;
    private final MqSender mqSender;
    private final ApplyOrderConvert applyOrderConvert;

    @Autowired
    public ApplyOrderServiceImpl(ApplyOrderRepository applyOrderRepository,
                                  CompanyGateway companyGateway,
                                  MqSender mqSender,
                                  ApplyOrderConvert applyOrderConvert) {
        this.applyOrderRepository = applyOrderRepository;
        this.companyGateway = companyGateway;
        this.mqSender = mqSender;
        this.applyOrderConvert = applyOrderConvert;
    }

    @Override
    public Long submitApplyOrder(SubmitApplyOrderDTO dto) {
        // 1. 通过 Gateway 远程查询公司信息
        CompanyDTO companyDTO = companyGateway.findByCompanyId(dto.getCompanyId());

        // 2. 通过 Factory 创建聚合根
        ApplyOrderEntity applyOrder = ApplyOrderFactory.createApplyOrder(dto);

        // 3. 通过 Repository 持久化聚合根
        Long id = applyOrderRepository.save(applyOrder);

        // 4. 通过 Convert 转换事件并发送 MQ 消息
        mqSender.send(JSONObject.toJSONString(
                applyOrderConvert.convertApplyOrderSubmittedEvent(applyOrder)));

        return id;
    }

    @Override
    public void sendExpress(SendExpressCommand sendExpressCommand) {
        // 1. 查询聚合根
        ApplyOrderEntity applyOrderEntity = applyOrderRepository.findById(sendExpressCommand.getApplyOrderId());

        // 2. 快照旧实体（用于后续对比变更）
        ApplyOrderEntity oldApplyOrderEntity = ApplyOrderFactory.clone(applyOrderEntity);

        // 3. 调用聚合根的领域方法
        applyOrderEntity.sendExpress(sendExpressCommand.getExpressNo());

        // 4. 保存变更后的聚合根
        applyOrderRepository.update(oldApplyOrderEntity, applyOrderEntity);

        // 5. 发送事件
        mqSender.send(JSONObject.toJSONString(
                applyOrderConvert.convertExpressSentEvent(applyOrderEntity)));
    }
}
