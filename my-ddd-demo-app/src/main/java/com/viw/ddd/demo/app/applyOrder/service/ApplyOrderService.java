package com.viw.ddd.demo.app.applyOrder.service;

import com.viw.ddd.demo.app.applyOrder.dto.SendExpressDTO;
import com.viw.ddd.demo.app.applyOrder.dto.SubmitApplyOrderDTO;

/**
 * 【DDD - 应用层（Application）· 应用服务接口（Application Service Interface）】
 *
 * 应用服务是 DDD 四层架构中的"指挥中心"：
 *   1. 接收来自 API/Adapter 层的请求（DTO 形式，已过防腐层）
 *   2. 编排领域对象（聚合根、值对象、仓储、领域服务）完成业务流程
 *   3. 不包含业务逻辑（业务逻辑在 domain 层）
 *   4. 负责事务管理、事件发布等横切关注点
 *
 * 防腐设计：
 *   submitApplyOrder 接收的是 app 层的 DTO（非 api 层的 Command），
 *   上游 RPC/HTTP 接口字段变更只影响 Command + Assembler，不影响本接口。
 *
 * @author xhb
 */
public interface ApplyOrderService {

    /** 提交申请单（参数经过防腐层转为 DTO） */
    Long submitApplyOrder(SubmitApplyOrderDTO dto);

    /** 发送快递（参数经过防腐层转为 DTO） */
    void sendExpress(SendExpressDTO dto);
}
