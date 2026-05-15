package com.viw.ddd.demo.app.applyOrder.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.viw.ddd.demo.api.applyOrder.dto.SendExpressCommand;
import com.viw.ddd.demo.api.applyOrder.dto.SubmitApplyOrderCommand;
import com.viw.ddd.demo.app.applyOrder.convert.ApplyOrderConvert;
import com.viw.ddd.demo.app.applyOrder.factory.ApplyOrderFactory;
import com.viw.ddd.demo.app.applyOrder.service.ApplyOrderService;
import com.viw.ddd.demo.domain.applyOrder.entity.ApplyOrderEntity;
import com.viw.ddd.demo.domain.applyOrder.repository.ApplyOrderRepository;
import com.viw.ddd.demo.infra.company.CompanyGateway;
import com.viw.ddd.demo.infra.company.dto.CompanyDTO;
import com.viw.ddd.demo.infra.util.mq.MqSender;

/**
 * 【DDD - 应用层（Application）· 应用服务实现】
 *
 * 应用服务是典型的"编排"角色：
 *   1. submitApplyOrder: 查询公司 → 构造实体 → 保存 → 发事件
 *   2. sendExpress:    查询实体 → 快照(old) → 执行业务 → 对比更新 → 发事件
 *
 * 典型模式：取出聚合根 → 调用聚合根方法 → 保存聚合根
 * 这是 DDD 中"事务脚本"在应用层的表现，真正的业务逻辑在实体内部。
 *
 * @author xhb
 */
public class ApplyOrderServiceImpl implements ApplyOrderService {

    private ApplyOrderRepository applyOrderRepository;
    private CompanyGateway companyGateway;
    private MqSender mqSender;
    private ApplyOrderConvert applyOrderConvert;

    @Override
    public Long submitApplyOrder(SubmitApplyOrderCommand submitApplyOrderCommand) {
        // 1. 通过 Gateway 远程查询公司信息
        CompanyDTO companyDTO = companyGateway.findByCompanyId(submitApplyOrderCommand.getCompanyId());

        // 2. 通过 Factory 创建聚合根
        ApplyOrderEntity applyOrder = ApplyOrderFactory.createApplyOrder(submitApplyOrderCommand);

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

        // 2. 快照旧实体（用于后续对比变更，类似 Hibernate 的快照机制）
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
