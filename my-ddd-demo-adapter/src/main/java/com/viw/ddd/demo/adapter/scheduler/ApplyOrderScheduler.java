package com.viw.ddd.demo.adapter.scheduler;

import com.viw.ddd.demo.api.applyOrder.dto.ApplyOrderQueryDTO;
import com.viw.ddd.demo.app.applyOrder.service.ApplyOrderQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 【DDD - 适配层（Adapter）· 定时任务】
 *
 * DDD 分层视角下的定时任务定位：
 *   定时任务是"外部触发"机制，类似 Controller 和 MQ 消费者，都属于适配层。
 *   它们统一调用应用层服务，不直接操作领域对象或仓储。
 *
 * 实际项目中：
 *   - 可以用 @Scheduled(cron = "0 0/5 * * * ?") 声明 Cron 表达式
 *   - 定时任务的调度逻辑（如分布式锁、任务分片）属于基础设施层
 *
 * @author xhb
 */
@Component
public class ApplyOrderScheduler {

    private static final Logger log = LoggerFactory.getLogger(ApplyOrderScheduler.class);

    private final ApplyOrderQueryService applyOrderQueryService;

    @Autowired
    public ApplyOrderScheduler(ApplyOrderQueryService applyOrderQueryService) {
        this.applyOrderQueryService = applyOrderQueryService;
    }

    /**
     * 模拟定时检查超时未审批的申请单
     * 生产环境建议使用分布式任务调度（如 XXL-JOB、SchedulerX）
     */
    @Scheduled(fixedDelay = 60000)
    public void checkTimeoutApplyOrders() {
        log.info("[定时任务] 开始检查超时申请单...");
        // 模拟：遍历待审批的申请单
        // List<Long> timeoutIds = applyOrderQueryService.findTimeoutApplyOrders();
        // for (Long id : timeoutIds) {
        //     // 超时自动取消或通知
        // }
        log.info("[定时任务] 检查完成，未发现超时申请单");
    }

    /**
     * 模拟定时同步外部系统数据
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void syncExternalData() {
        log.info("[定时任务] 开始同步外部系统数据...");
        // 模拟：同步第三方审批系统的最新状态
        log.info("[定时任务] 外部数据同步完成");
    }
}
