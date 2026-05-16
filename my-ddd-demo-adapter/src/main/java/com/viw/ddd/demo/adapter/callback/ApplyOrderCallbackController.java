package com.viw.ddd.demo.adapter.callback;

import com.viw.ddd.demo.app.applyOrder.dto.SendExpressDTO;
import com.viw.ddd.demo.app.applyOrder.service.ApplyOrderService;
import com.viw.ddd.demo.common.dto.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 【DDD - 适配层（Adapter）· 第三方 HTTP 回调控制器】
 *
 * DDD 分层视角下的回调定位：
 *   第三方系统通过 HTTP 回调通知我们审批结果、支付结果等。
 *   回调是外部入口（等同于 API 调用），属于适配层职责。
 *   适配层只做参数接收和转换，业务逻辑委托给应用层。
 *
 * @author xhb
 */
@RestController
@RequestMapping("/callback")
public class ApplyOrderCallbackController {

    private static final Logger log = LoggerFactory.getLogger(ApplyOrderCallbackController.class);

    private final ApplyOrderService applyOrderService;

    @Autowired
    public ApplyOrderCallbackController(ApplyOrderService applyOrderService) {
        this.applyOrderService = applyOrderService;
    }

    /**
     * 接收第三方审批系统的审批结果回调
     *
     * @param body 回调 JSON，模拟结构: {"applyOrderId": 1, "approved": true}
     */
    @PostMapping("/apply-order/approval")
    public Result<?> approvalCallback(@RequestBody Map<String, Object> body) {
        Long applyOrderId = Long.valueOf(body.get("applyOrderId").toString());
        Boolean approved = (Boolean) body.getOrDefault("approved", false);

        log.info("[回调] 收到审批结果回调: applyOrderId={}, approved={}", applyOrderId, approved);

        if (approved) {
            // 审批通过后触发快递寄送（模拟）
            SendExpressDTO dto = new SendExpressDTO();
            dto.setApplyOrderId(applyOrderId);
            applyOrderService.sendExpress(dto);
            log.info("[回调] 审批通过，已触发快递寄送: applyOrderId={}", applyOrderId);
        }

        return Result.success();
    }
}
