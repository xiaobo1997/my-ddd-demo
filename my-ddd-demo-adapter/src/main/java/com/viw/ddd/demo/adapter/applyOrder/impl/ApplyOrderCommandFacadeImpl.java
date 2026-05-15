package com.viw.ddd.demo.adapter.applyOrder.impl;

import com.viw.ddd.demo.api.applyOrder.ApplyOrderCommandFacade;
import com.viw.ddd.demo.api.applyOrder.dto.SendExpressCommand;
import com.viw.ddd.demo.api.applyOrder.dto.SubmitApplyOrderCommand;
import com.viw.ddd.demo.app.applyOrder.service.ApplyOrderService;

/**
 * 【DDD - 适配层（Adapter）· 门面实现】
 *
 * Adapter 层是 DDD 中负责"适配"的层：
 *   1. 实现 API 层定义的接口（Facade）
 *   2. 将外部请求（HTTP/RPC）转译为应用层可理解的调用
 *   3. 负责参数校验、结果包装等接入层逻辑
 *
 * 典型流程：Controller 请求 → FacadeImpl → ApplicationService → Domain
 * 
 * 注意：本层不包含业务逻辑，只做"转译"
 *
 * @author xhb
 */
public class ApplyOrderCommandFacadeImpl implements ApplyOrderCommandFacade {

    private ApplyOrderService applyOrderService;

    @Override
    public Long submitApplyOrder(SubmitApplyOrderCommand command) {
        // 转译：API 层的 Command 直接传递给应用层
        return applyOrderService.submitApplyOrder(command);
    }

    @Override
    public void sendExpress(SendExpressCommand command) {
        applyOrderService.sendExpress(command);
    }
}
