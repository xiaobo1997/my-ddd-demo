package com.viw.ddd.demo.app.applyOrder.service;

import com.viw.ddd.demo.api.applyOrder.dto.SendExpressCommand;
import com.viw.ddd.demo.api.applyOrder.dto.SubmitApplyOrderCommand;

/**
 * @author xhb
 * @Date 2026/1/8
 * @Description :
 */

public interface ApplyOrderService {

    Long submitApplyOrder(SubmitApplyOrderCommand submitApplyOrderCommand);

    void sendExpress(SendExpressCommand sendExpressCommand);

}
