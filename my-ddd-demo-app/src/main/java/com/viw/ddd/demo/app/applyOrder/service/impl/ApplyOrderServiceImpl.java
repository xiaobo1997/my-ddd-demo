package com.viw.ddd.demo.app.applyOrder.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.viw.ddd.demo.api.applyOrder.dto.SendExpressCommand;
import com.viw.ddd.demo.api.applyOrder.dto.SubmitApplyOrderCommand;
import com.viw.ddd.demo.app.applyOrder.convert.ApplyOrderConvert;
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
 * 应用服务典型是的"编排"角色：
 *   1. submitApplyOrder: 查询公司 → 构造实体 → 保存 → 发事件
 *   2. sendExpress:    查询实体 → 快照(old) → 执行业务 → 对比更新 → 发事件
 *
 * 典型模式：取出聚合根 → 调用聚合根方法 → 保存聚合根
 * 这是 DDD 中"事务脚本"在应用层的表现，真正的业务逻辑在实体内部。
 *
 * DDD 依赖方向（已修复）：
 *   ✅ 只依赖 domain 层接口（CompanyGateway、MqSender、ApplyOrderRepository）
 *   ✅ 不依赖 infra 层实现 —— 通过构造函数注入，Spring 自动装配
 *   ✅ 依赖倒置：app → domain(接口) ← infra(实现)
 *
 * @author xhb
 */
@Service
public class ApplyOrderServiceImpl implements ApplyOrderService {

    private final ApplyOrderRepository applyOrderRepository;
    private final CompanyGateway companyGateway;
    private final MqSender mqSender;
    private final ApplyOrderConvert applyOrderConvert;

    /**
     * 构造函数注入（推荐方式，优于 @Autowired 字段注入）
     * Spring 会自动从容器中查找对应实现：
     *   - ApplyOrderRepository → ApplyOrderRepositoryImpl (@Repository)
     *   - CompanyGateway → CompanyGatewayImpl (@Component)
     *   - MqSender → MqSenderImpl (@Component)
     *   - ApplyOrderConvert → ApplyOrderConvertImpl (@Component)
     */
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
