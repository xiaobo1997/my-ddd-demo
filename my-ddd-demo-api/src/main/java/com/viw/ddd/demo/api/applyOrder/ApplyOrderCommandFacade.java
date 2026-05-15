package com.viw.ddd.demo.api.applyOrder;

import com.viw.ddd.demo.api.applyOrder.dto.SendExpressCommand;
import com.viw.ddd.demo.api.applyOrder.dto.SubmitApplyOrderCommand;

/**
 * @author xhb
 * @Date 2026/1/8
 * @Description :
 */

public interface ApplyOrderCommandFacade {

    Long submitApplyOrder(SubmitApplyOrderCommand command);

    void sendExpress(SendExpressCommand command);

}
