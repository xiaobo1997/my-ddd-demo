package com.viw.ddd.demo.api.applyOrder;

import com.viw.ddd.demo.api.applyOrder.dto.SendExpressCommand;
import com.viw.ddd.demo.api.applyOrder.dto.SubmitApplyOrderCommand;

/**
 * 【DDD - 接口层（API）】
 *
 * 对外暴露的 Command 门面接口。
 * 职责：定义外部系统（Controller、RPC 客户端等）可以调用的业务方法。
 * 命名规范：XxxCommandFacade，放在 api 模块中。
 *
 * 注意：本层只定义接口契约，不包含任何实现。
 *       实现放在 adapter 模块的 impl 包中。
 *
 * @author xhb
 */
public interface ApplyOrderCommandFacade {

    /**
     * 提交申请单
     * @param command 申请单提交命令（包含公司ID、发票抬头、金额等）
     * @return 申请单ID
     */
    Long submitApplyOrder(SubmitApplyOrderCommand command);

    /**
     * 发送快递（纸质发票邮寄）
     * @param command 快递发送命令（包含申请单ID、快递单号）
     */
    void sendExpress(SendExpressCommand command);
}
