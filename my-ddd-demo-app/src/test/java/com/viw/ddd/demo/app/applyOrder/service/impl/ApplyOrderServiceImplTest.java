package com.viw.ddd.demo.app.applyOrder.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.viw.ddd.demo.app.applyOrder.convert.ApplyOrderConvert;
import com.viw.ddd.demo.app.applyOrder.dto.SendExpressDTO;
import com.viw.ddd.demo.app.applyOrder.dto.SubmitApplyOrderDTO;
import com.viw.ddd.demo.app.applyOrder.event.publish.ApplyOrderSubmittedEvent;
import com.viw.ddd.demo.app.applyOrder.event.publish.ExpressSentEvent;
import com.viw.ddd.demo.domain.applyOrder.entity.ApplyOrderEntity;
import com.viw.ddd.demo.domain.applyOrder.repository.ApplyOrderRepository;
import com.viw.ddd.demo.domain.company.CompanyDTO;
import com.viw.ddd.demo.domain.gateway.CompanyGateway;
import com.viw.ddd.demo.domain.gateway.MqSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * ApplyOrderServiceImpl Mockito 单元测试 —— 验证应用服务的编排逻辑。
 *
 * 测试策略：
 *   1. Mock 全部外部依赖（Gateway / Repository / MQ / Convert）
 *   2. 验证调用顺序和参数是否正确
 *   3. 不关心领域对象内部逻辑（那是 Entity 测试的职责）
 *
 * 面试要点：
 *   "应用层测试用 Mockito 隔离外部依赖，验证编排流程，
 *    领域层测试直接测 Entity 不需要 Mock——这是分层测试的核心思想。"
 *
 * @author xhb
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ApplyOrderServiceImpl 应用服务测试")
class ApplyOrderServiceImplTest {

    @Mock private ApplyOrderRepository applyOrderRepository;
    @Mock private CompanyGateway companyGateway;
    @Mock private MqSender mqSender;
    @Mock private ApplyOrderConvert applyOrderConvert;

    @InjectMocks
    private ApplyOrderServiceImpl service;

    // ==================== submitApplyOrder ====================

    @Nested
    @DisplayName("submitApplyOrder — 提交申请单")
    class SubmitApplyOrder {

        @Test
        @DisplayName("应依次调用 Gateway → Repository.save → MQ.send，并返回 ID")
        void shouldOrchestrateSubmitFlow() {
            // given
            SubmitApplyOrderDTO dto = buildDTO();
            CompanyDTO company = CompanyDTO.builder().companyId(1L).build();
            ApplyOrderEntity savedEntity = ApplyOrderEntity.builder().id(100L).build();
            ApplyOrderSubmittedEvent event = new ApplyOrderSubmittedEvent();
            event.setApplyOrderId(100L);

            when(companyGateway.findByCompanyId(1L)).thenReturn(company);
            when(applyOrderRepository.save(any(ApplyOrderEntity.class))).thenReturn(100L);
            when(applyOrderConvert.convertApplyOrderSubmittedEvent(any(ApplyOrderEntity.class)))
                    .thenReturn(event);

            // when
            Long result = service.submitApplyOrder(dto);

            // then
            assertEquals(100L, result, "应返回 Repository 生成的 ID");

            // 验证调用顺序（inOrder 确保编排正确）
            InOrder inOrder = inOrder(companyGateway, applyOrderRepository, mqSender, applyOrderConvert);
            inOrder.verify(companyGateway).findByCompanyId(1L);
            inOrder.verify(applyOrderRepository).save(any(ApplyOrderEntity.class));
            inOrder.verify(applyOrderConvert).convertApplyOrderSubmittedEvent(any(ApplyOrderEntity.class));
            inOrder.verify(mqSender).send(anyString());
        }

        @Test
        @DisplayName("Gateway 查询公司信息时传入正确的 companyId")
        void shouldPassCorrectCompanyIdToGateway() {
            SubmitApplyOrderDTO dto = buildDTO();
            dto.setCompanyId(42L);

            when(companyGateway.findByCompanyId(anyLong()))
                    .thenReturn(CompanyDTO.builder().companyId(42L).build());
            when(applyOrderRepository.save(any())).thenReturn(1L);
            when(applyOrderConvert.convertApplyOrderSubmittedEvent(any()))
                    .thenReturn(new ApplyOrderSubmittedEvent());

            service.submitApplyOrder(dto);

            verify(companyGateway).findByCompanyId(42L);
        }

        @Test
        @DisplayName("MQ 发送的消息应包含申请单 ID")
        void shouldSendEventWithOrderId() {
            SubmitApplyOrderDTO dto = buildDTO();
            ApplyOrderSubmittedEvent event = new ApplyOrderSubmittedEvent();
            event.setApplyOrderId(999L);

            when(companyGateway.findByCompanyId(anyLong()))
                    .thenReturn(CompanyDTO.builder().build());
            when(applyOrderRepository.save(any())).thenReturn(999L);
            when(applyOrderConvert.convertApplyOrderSubmittedEvent(any())).thenReturn(event);

            ArgumentCaptor<String> msgCaptor = ArgumentCaptor.forClass(String.class);
            service.submitApplyOrder(dto);

            verify(mqSender).send(msgCaptor.capture());
            String sentMsg = msgCaptor.getValue();
            assertTrue(sentMsg.contains("999"),
                    "MQ 消息应包含申请单 ID 999，实际: " + sentMsg);
        }

        private SubmitApplyOrderDTO buildDTO() {
            SubmitApplyOrderDTO dto = new SubmitApplyOrderDTO();
            dto.setCompanyId(1L);
            dto.setInvoiceHeader("测试公司");
            dto.setSubject("测试申请");
            dto.setApplyAmount(new BigDecimal("10000"));
            dto.setFreightFee(new BigDecimal("500"));
            dto.setServiceFee(new BigDecimal("200"));
            return dto;
        }
    }

    // ==================== sendExpress ====================

    @Nested
    @DisplayName("sendExpress — 发送快递")
    class SendExpress {

        @Test
        @DisplayName("应依次调用 findById → sendExpress → update → MQ.send")
        void shouldOrchestrateSendExpressFlow() {
            // given
            SendExpressDTO dto = new SendExpressDTO();
            dto.setApplyOrderId(1L);
            dto.setExpressNo("SF1234567890");

            ApplyOrderEntity entity = ApplyOrderEntity.builder()
                    .id(1L)
                    .applyOrderNo("AP20260515000001")
                    .status(com.viw.ddd.demo.common.enums.ApplyOrderStatusEnum.SENT_MAIL)
                    .build();
            ExpressSentEvent event = ExpressSentEvent.builder()
                    .applyOrderId(1L)
                    .build();

            when(applyOrderRepository.findById(1L)).thenReturn(entity);
            when(applyOrderRepository.update(any(), any())).thenReturn(1);
            when(applyOrderConvert.convertExpressSentEvent(any())).thenReturn(event);

            // when
            service.sendExpress(dto);

            // then
            InOrder inOrder = inOrder(applyOrderRepository, mqSender, applyOrderConvert);
            inOrder.verify(applyOrderRepository).findById(1L);
            inOrder.verify(applyOrderRepository).update(any(), any());
            inOrder.verify(applyOrderConvert).convertExpressSentEvent(any());
            inOrder.verify(mqSender).send(anyString());
        }

        @Test
        @DisplayName("update 时应传入旧快照和新实体两个参数")
        void shouldPassOldAndNewEntityToUpdate() {
            SendExpressDTO dto = new SendExpressDTO();
            dto.setApplyOrderId(1L);
            dto.setExpressNo("SF123");

            ApplyOrderEntity entity = ApplyOrderEntity.builder().id(1L)
                    .status(com.viw.ddd.demo.common.enums.ApplyOrderStatusEnum.SENT_MAIL)
                    .build();
            when(applyOrderRepository.findById(1L)).thenReturn(entity);
            when(applyOrderRepository.update(any(), any())).thenReturn(1);
            when(applyOrderConvert.convertExpressSentEvent(any()))
                    .thenReturn(ExpressSentEvent.builder().build());

            service.sendExpress(dto);

            ArgumentCaptor<ApplyOrderEntity> oldCaptor = ArgumentCaptor.forClass(ApplyOrderEntity.class);
            ArgumentCaptor<ApplyOrderEntity> newCaptor = ArgumentCaptor.forClass(ApplyOrderEntity.class);
            verify(applyOrderRepository).update(oldCaptor.capture(), newCaptor.capture());

            assertNotNull(oldCaptor.getValue(), "旧快照不应为 null");
            assertNotNull(newCaptor.getValue(), "新实体不应为 null");
            // 快照是 clone 出来的，应该是不同对象
            assertNotSame(oldCaptor.getValue(), newCaptor.getValue(),
                    "旧快照和新实体应该是不同对象（深拷贝）");
        }
    }
}
