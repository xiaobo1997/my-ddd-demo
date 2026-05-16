package com.viw.ddd.demo.adapter.applyOrder.impl;

import com.viw.ddd.demo.api.applyOrder.ApplyOrderCommandFacade;
import com.viw.ddd.demo.api.applyOrder.dto.SendExpressCommand;
import com.viw.ddd.demo.api.applyOrder.dto.SubmitApplyOrderCommand;
import com.viw.ddd.demo.app.applyOrder.assembler.ApplyOrderAssembler;
import com.viw.ddd.demo.app.applyOrder.dto.SendExpressDTO;
import com.viw.ddd.demo.app.applyOrder.dto.SubmitApplyOrderDTO;
import com.viw.ddd.demo.app.applyOrder.service.ApplyOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 【DDD - 适配层（Adapter）· 命令门面实现 + 防腐层入口】
 *
 * 职责：
 *   1. 实现 api 层的 ApplyOrderCommandFacade
 *   2. 防腐处理：通过 ApplyOrderAssembler（MapStruct）将 api 的 Command 转为 app 的 DTO
 *   3. 转调应用层服务
 *
 * 防腐设计关键：
 *   Command（外部契约）→ [Assembler 映射] → DTO（内部模型）→ Service
 *   上游改字段 → 只影响 Command + Assembler，Service 不受影响
 *
 * @author xhb
 */
@Component
public class ApplyOrderCommandFacadeImpl implements ApplyOrderCommandFacade {

    private final ApplyOrderAssembler assembler;
    private final ApplyOrderService applyOrderService;

    @Autowired
    public ApplyOrderCommandFacadeImpl(ApplyOrderAssembler assembler,
                                        ApplyOrderService applyOrderService) {
        this.assembler = assembler;
        this.applyOrderService = applyOrderService;
    }

    @Override
    public Long submitApplyOrder(SubmitApplyOrderCommand command) {
        // 防腐：Command → DTO
        SubmitApplyOrderDTO dto = assembler.toDTO(command);
        // 转调应用层服务
        return applyOrderService.submitApplyOrder(dto);
    }

    @Override
    public void sendExpress(SendExpressCommand command) {
        // 防腐：Command → DTO
        SendExpressDTO dto = assembler.toDTO(command);
        // 转调应用层服务
        applyOrderService.sendExpress(dto);
    }
}
