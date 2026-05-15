package com.viw.ddd.demo.app.applyOrder.service;

import com.viw.ddd.demo.api.applyOrder.dto.SendExpressCommand;
import com.viw.ddd.demo.api.applyOrder.dto.SubmitApplyOrderCommand;

/**
 * 【DDD - 应用层（Application）· 应用服务接口（Application Service Interface）】
 *
 * 应用服务是 DDD 四层架构中的"指挥中心"：
 *   1. 接收来自 API/Adapter 层的请求（以 Command 的形式）
 *   2. 编排领域对象（聚合根、值对象、仓储、领域服务）完成业务流程
 *   3. 不包含业务逻辑（业务逻辑在 domain 层）
 *   4. 负责事务管理、事件发布等横切关注点
 *
 * @author xhb
 */
public interface ApplyOrderService {

    /** 提交申请单 */
    Long submitApplyOrder(SubmitApplyOrderCommand submitApplyOrderCommand);

    /** 发送快递 */
    void sendExpress(SendExpressCommand sendExpressCommand);
}
