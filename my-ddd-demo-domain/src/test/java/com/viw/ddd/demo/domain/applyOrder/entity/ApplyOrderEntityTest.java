package com.viw.ddd.demo.domain.applyOrder.entity;

import com.viw.ddd.demo.common.enums.ApplyOrderStatusEnum;
import com.viw.ddd.demo.domain.applyOrder.vo.ApplyOrderExpressVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ApplyOrderEntity 单元测试 —— 覆盖所有状态流转和业务方法。
 *
 * DDD 面试要点：
 *   领域层的 Entity 测试不需要 Spring 容器、不需要 Mock，
 *   纯 POJO 测试，跑得飞快。这就是"领域层不依赖框架"的优势。
 *
 * @author xhb
 */
@DisplayName("ApplyOrderEntity 状态流转测试")
class ApplyOrderEntityTest {

    private ApplyOrderEntity entity;

    @BeforeEach
    void setUp() {
        entity = new ApplyOrderEntity();
    }

    // ==================== create() ====================

    @Nested
    @DisplayName("create() — 初始化申请单")
    class Create {

        @Test
        @DisplayName("应生成申请单号 + 设置草稿状态 + 设置申请日期")
        void shouldInitializeWithDraftStatus() {
            entity.create();

            assertNotNull(entity.getApplyOrderNo(), "申请单号不应为空");
            assertTrue(entity.getApplyOrderNo().startsWith("AP"), "申请单号应以 AP 开头");
            assertEquals(ApplyOrderStatusEnum.DRAFT, entity.getStatus(), "状态应为 DRAFT");
            assertNotNull(entity.getApplyDate(), "申请日期不应为空");
        }

        @Test
        @DisplayName("申请单号格式应为 AP + yyyyMMdd + 6位随机数")
        void shouldGenerateCorrectOrderNoFormat() {
            entity.create();

            String orderNo = entity.getApplyOrderNo();
            assertTrue(orderNo.matches("AP\\d{14}"),
                    "申请单号格式应为 AP + 14位数字（8位日期 + 6位随机），实际: " + orderNo);
        }
    }

    // ==================== approve() ====================

    @Nested
    @DisplayName("approve() — 审批通过")
    class Approve {

        @Test
        @DisplayName("DRAFT → APPROVED，正常流转")
        void shouldTransitionFromDraftToApproved() {
            entity.setStatus(ApplyOrderStatusEnum.DRAFT);
            entity.approve();
            assertEquals(ApplyOrderStatusEnum.APPROVED, entity.getStatus());
        }

        @Test
        @DisplayName("非 DRAFT 状态审批应抛异常")
        void shouldThrowWhenNotDraft() {
            entity.setStatus(ApplyOrderStatusEnum.APPROVED);
            assertThrows(IllegalStateException.class, () -> entity.approve());

            entity.setStatus(ApplyOrderStatusEnum.SENT_EXPRESS);
            assertThrows(IllegalStateException.class, () -> entity.approve());
        }
    }

    // ==================== createBatch() ====================

    @Nested
    @DisplayName("createBatch() — 创建开票批次")
    class CreateBatch {

        @Test
        @DisplayName("APPROVED → CREATE_BATCH，正常流转")
        void shouldTransitionFromApprovedToCreateBatch() {
            entity.setStatus(ApplyOrderStatusEnum.APPROVED);
            entity.createBatch();
            assertEquals(ApplyOrderStatusEnum.CREATE_BATCH, entity.getStatus());
        }

        @Test
        @DisplayName("非 APPROVED 状态创建批次应抛异常")
        void shouldThrowWhenNotApproved() {
            entity.setStatus(ApplyOrderStatusEnum.DRAFT);
            assertThrows(IllegalStateException.class, () -> entity.createBatch());

            entity.setStatus(ApplyOrderStatusEnum.CREATE_BATCH);
            assertThrows(IllegalStateException.class, () -> entity.createBatch());
        }
    }

    // ==================== finishInvoice() ====================

    @Nested
    @DisplayName("finishInvoice() — 完成开票")
    class FinishInvoice {

        @Test
        @DisplayName("CREATE_BATCH → INVOICE_FINISHED，正常流转")
        void shouldTransitionFromCreateBatchToInvoiceFinished() {
            entity.setStatus(ApplyOrderStatusEnum.CREATE_BATCH);
            entity.finishInvoice();
            assertEquals(ApplyOrderStatusEnum.INVOICE_FINISHED, entity.getStatus());
        }

        @Test
        @DisplayName("非 CREATE_BATCH 状态完成开票应抛异常")
        void shouldThrowWhenNotCreateBatch() {
            entity.setStatus(ApplyOrderStatusEnum.APPROVED);
            assertThrows(IllegalStateException.class, () -> entity.finishInvoice());
        }
    }

    // ==================== sendMail() ====================

    @Nested
    @DisplayName("sendMail() — 寄送电子发票")
    class SendMail {

        @Test
        @DisplayName("INVOICE_FINISHED → SENT_MAIL，正常流转")
        void shouldTransitionFromInvoiceFinishedToSentMail() {
            entity.setStatus(ApplyOrderStatusEnum.INVOICE_FINISHED);
            entity.sendMail();
            assertEquals(ApplyOrderStatusEnum.SENT_MAIL, entity.getStatus());
        }

        @Test
        @DisplayName("非 INVOICE_FINISHED 状态发送邮件应抛异常")
        void shouldThrowWhenNotInvoiceFinished() {
            entity.setStatus(ApplyOrderStatusEnum.CREATE_BATCH);
            assertThrows(IllegalStateException.class, () -> entity.sendMail());
        }
    }

    // ==================== sendExpress() ====================

    @Nested
    @DisplayName("sendExpress() — 快递纸质发票")
    class SendExpress {

        @Test
        @DisplayName("SENT_MAIL → SENT_EXPRESS，正常流转")
        void shouldTransitionFromSentMailToSentExpress() {
            entity.setStatus(ApplyOrderStatusEnum.SENT_MAIL);
            entity.sendExpress("SF1234567890");
            assertEquals(ApplyOrderStatusEnum.SENT_EXPRESS, entity.getStatus());
        }

        @Test
        @DisplayName("应调用 VO 的 send() 设置快递单号")
        void shouldSetExpressNoOnVO() {
            ApplyOrderExpressVO expressVO = new ApplyOrderExpressVO();
            entity.setApplyOrderExpressVO(expressVO);
            entity.setStatus(ApplyOrderStatusEnum.SENT_MAIL);

            entity.sendExpress("SF1234567890");

            assertEquals("SF1234567890", expressVO.getExpressNo(), "VO 的快递单号应被设置");
        }

        @Test
        @DisplayName("VO 为 null 时不抛 NPE，正常推进状态")
        void shouldNotThrowWhenVONull() {
            entity.setApplyOrderExpressVO(null);
            entity.setStatus(ApplyOrderStatusEnum.SENT_MAIL);

            assertDoesNotThrow(() -> entity.sendExpress("SF1234567890"));
            assertEquals(ApplyOrderStatusEnum.SENT_EXPRESS, entity.getStatus());
        }

        @Test
        @DisplayName("非 SENT_MAIL 状态发送快递应抛异常")
        void shouldThrowWhenNotSentMail() {
            entity.setStatus(ApplyOrderStatusEnum.DRAFT);
            assertThrows(IllegalStateException.class, () -> entity.sendExpress("SF123"));
        }
    }

    // ==================== 完整生命周期 ====================

    @Nested
    @DisplayName("完整状态流转链路")
    class FullLifecycle {

        @Test
        @DisplayName("create → approve → createBatch → finishInvoice → sendMail → sendExpress 全链路")
        void shouldCompleteFullLifecycle() {
            // 初始化
            entity.create();
            assertEquals(ApplyOrderStatusEnum.DRAFT, entity.getStatus());

            // 审批
            entity.approve();
            assertEquals(ApplyOrderStatusEnum.APPROVED, entity.getStatus());

            // 创建批次
            entity.createBatch();
            assertEquals(ApplyOrderStatusEnum.CREATE_BATCH, entity.getStatus());

            // 完成开票
            entity.finishInvoice();
            assertEquals(ApplyOrderStatusEnum.INVOICE_FINISHED, entity.getStatus());

            // 发送邮件
            entity.sendMail();
            assertEquals(ApplyOrderStatusEnum.SENT_MAIL, entity.getStatus());

            // 发送快递
            entity.sendExpress("SF1234567890");
            assertEquals(ApplyOrderStatusEnum.SENT_EXPRESS, entity.getStatus());
        }

        @Test
        @DisplayName("create 方法可重复调用（重新初始化）")
        void shouldAllowRecreate() {
            entity.create();
            String firstOrderNo = entity.getApplyOrderNo();

            entity.create();
            String secondOrderNo = entity.getApplyOrderNo();

            assertNotEquals(firstOrderNo, secondOrderNo, "每次 create 应生成新申请单号");
            assertEquals(ApplyOrderStatusEnum.DRAFT, entity.getStatus());
        }
    }
}
