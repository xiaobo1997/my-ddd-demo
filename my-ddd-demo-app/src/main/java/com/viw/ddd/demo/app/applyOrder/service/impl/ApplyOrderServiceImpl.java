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
 * @author xhb
 * @Date 2026/1/8
 * @Description :
 */

public class ApplyOrderServiceImpl implements ApplyOrderService {

    private ApplyOrderRepository applyOrderRepository;
    private CompanyGateway companyGateway;
    private MqSender mqSender;

    private ApplyOrderConvert applyOrderConvert;

    // todo
    @Override
    public Long submitApplyOrder(SubmitApplyOrderCommand submitApplyOrderCommand) {
        //查询客户信息
        CompanyDTO companyDTO = companyGateway.findByCompanyId(submitApplyOrderCommand.getCompanyId());
        //创建申请
        ApplyOrderEntity applyOrder = ApplyOrderFactory.createApplyOrder(submitApplyOrderCommand);
        //保存
        Long id = applyOrderRepository.save(applyOrder);
        //发送信息
        mqSender.send(JSONObject.toJSONString(applyOrderConvert.convertApplyOrderSubmittedEvent(applyOrder)));
        return id;
    }

    @Override
    public void sendExpress(SendExpressCommand sendExpressCommand) {
        //查询申请单的信息
        ApplyOrderEntity applyOrderEntity = applyOrderRepository.findById(sendExpressCommand.getApplyOrderId());
        ApplyOrderEntity oldApplyOrderEntity = ApplyOrderFactory.clone(applyOrderEntity);
        //执行业务逻辑
        applyOrderEntity.sendExpress(sendExpressCommand.getExpressNo());

        //更新
        applyOrderRepository.update(oldApplyOrderEntity,applyOrderEntity);
        mqSender.send(JSONObject.toJSONString(applyOrderConvert.convertExpressSentEvent(applyOrderEntity)));
    }
}
