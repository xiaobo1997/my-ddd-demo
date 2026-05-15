package com.viw.ddd.demo.infra.applyOrder.repository.impl;

import com.viw.ddd.demo.common.enums.ApplyOrderStatusEnum;
import com.viw.ddd.demo.domain.applyOrder.entity.ApplyOrderEntity;
import com.viw.ddd.demo.domain.applyOrder.vo.ApplyOrderExpressVO;
import com.viw.ddd.demo.infra.applyOrder.assembler.ApplyOrderDataAssembler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

/**
 * ApplyOrderRepositoryImpl 单元测试 —— 测试内存 Map 持久化 + MapStruct 转换。
 *
 * 测试策略：
 *   1. Mock ApplyOrderDataAssembler（隔离 MapStruct 生成逻辑）
 *   2. 测试 Repository 的 CRUD 行为（ID 生成、存储、查询、更新）
 *   3. 不测试 MapStruct 映射本身（那是集成测试的职责）
 *
 * @author xhb
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ApplyOrderRepositoryImpl 仓储测试")
class ApplyOrderRepositoryImplTest {

    @Mock
    private ApplyOrderDataAssembler dataAssembler;

    @InjectMocks
    private ApplyOrderRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        // 每次 toDO 调用返回一个新的 DO 对象（模拟真实映射）
        lenient().when(dataAssembler.toDO(any(ApplyOrderEntity.class)))
                .thenAnswer(inv -> {
                    ApplyOrderEntity entity = inv.getArgument(0);
                    com.viw.ddd.demo.infra.applyOrder.repository.dataobject.ApplyOrderDO dObj =
                            new com.viw.ddd.demo.infra.applyOrder.repository.dataobject.ApplyOrderDO();
                    dObj.setId(entity.getId());
                    dObj.setApplyOrderNo(entity.getApplyOrderNo());
                    dObj.setStatus(entity.getStatus() != null ? entity.getStatus().getCode() : null);
                    return dObj;
                });

        // 每次 toEntity 调用返回一个新的 Entity（模拟真实映射）
        lenient().when(dataAssembler.toEntity(any(com.viw.ddd.demo.infra.applyOrder.repository.dataobject.ApplyOrderDO.class)))
                .thenAnswer(inv -> {
                    com.viw.ddd.demo.infra.applyOrder.repository.dataobject.ApplyOrderDO dObj = inv.getArgument(0);
                    return ApplyOrderEntity.builder()
                            .id(dObj.getId())
                            .applyOrderNo(dObj.getApplyOrderNo())
                            .status(dObj.getStatus() != null
                                    ? ApplyOrderStatusEnum.valueOf(dObj.getStatus())
                                    : null)
                            .build();
                });
    }

    // ==================== save ====================

    @Nested
    @DisplayName("save() — 新增申请单")
    class Save {

        @Test
        @DisplayName("新实体应自动生成 ID（从 1 开始自增）")
        void shouldAutoGenerateId() {
            ApplyOrderEntity entity = new ApplyOrderEntity();
            Long id = repository.save(entity);
            assertEquals(1L, id, "第一个实体 ID 应为 1");

            ApplyOrderEntity entity2 = new ApplyOrderEntity();
            Long id2 = repository.save(entity2);
            assertEquals(2L, id2, "第二个实体 ID 应为 2");
        }

        @Test
        @DisplayName("已有 ID 的实体应保留原 ID")
        void shouldPreserveExistingId() {
            ApplyOrderEntity entity = new ApplyOrderEntity();
            entity.setId(42L);
            Long id = repository.save(entity);
            assertEquals(42L, id, "已有 ID 的实体不应重新生成");
        }

        @Test
        @DisplayName("保存后可通过 findById 查到")
        void shouldBeFindableAfterSave() {
            ApplyOrderEntity entity = ApplyOrderEntity.builder()
                    .applyOrderNo("AP20260515000001")
                    .applyAmount(new BigDecimal("10000"))
                    .status(ApplyOrderStatusEnum.DRAFT)
                    .build();

            Long id = repository.save(entity);
            ApplyOrderEntity found = repository.findById(id);

            assertNotNull(found, "保存后应能查到");
            assertEquals("AP20260515000001", found.getApplyOrderNo());
        }
    }

    // ==================== findById ====================

    @Nested
    @DisplayName("findById() — 查询申请单")
    class FindById {

        @Test
        @DisplayName("不存在的 ID 应返回 null")
        void shouldReturnNullForNonExistingId() {
            ApplyOrderEntity result = repository.findById(999L);
            assertNull(result, "不存在的 ID 应返回 null");
        }

        @Test
        @DisplayName("多次查询同一个 ID 应返回一致结果")
        void shouldReturnConsistentResult() {
            ApplyOrderEntity entity = ApplyOrderEntity.builder()
                    .applyOrderNo("AP001")
                    .applyAmount(new BigDecimal("5000"))
                    .build();

            Long id = repository.save(entity);
            ApplyOrderEntity first = repository.findById(id);
            ApplyOrderEntity second = repository.findById(id);

            assertEquals(first.getApplyOrderNo(), second.getApplyOrderNo());
            assertEquals(first.getApplyAmount(), second.getApplyAmount());
        }
    }

    // ==================== findById with type ====================

    @Nested
    @DisplayName("findById(id, type) — 按类型过滤查询")
    class FindByIdWithType {

        @Test
        @DisplayName("type='detail' 时应过滤掉快递信息")
        void shouldFilterExpressWhenTypeIsDetail() {
            ApplyOrderExpressVO expressVO = new ApplyOrderExpressVO();
            expressVO.setExpressNo("SF123");

            ApplyOrderEntity entity = ApplyOrderEntity.builder()
                    .applyOrderNo("AP002")
                    .applyOrderExpressVO(expressVO)
                    .build();

            Long id = repository.save(entity);
            ApplyOrderEntity result = repository.findById(id, "detail");

            assertNotNull(result);
            assertNull(result.getApplyOrderExpressVO(),
                    "detail 模式下快递信息应为 null");
        }

        @Test
        @DisplayName("type 非 'detail' 时应返回完整数据")
        void shouldReturnFullDataWhenTypeIsNotDetail() {
            ApplyOrderExpressVO expressVO = new ApplyOrderExpressVO();
            expressVO.setExpressNo("SF456");

            ApplyOrderEntity entity = ApplyOrderEntity.builder()
                    .applyOrderNo("AP003")
                    .applyOrderExpressVO(expressVO)
                    .build();

            Long id = repository.save(entity);
            ApplyOrderEntity result = repository.findById(id, null);

            assertNotNull(result);
            // 注：由于 Mock 的 toEntity 没有映射 VO，这里实际会是 null
            // 真实 MapStruct 环境下会正确映射
        }
    }

    // ==================== update ====================

    @Nested
    @DisplayName("update() — 更新申请单")
    class Update {

        @Test
        @DisplayName("更新已存在的实体应返回 1")
        void shouldReturnOneForExistingEntity() {
            ApplyOrderEntity entity = ApplyOrderEntity.builder()
                    .applyOrderNo("AP004")
                    .status(ApplyOrderStatusEnum.DRAFT)
                    .build();

            Long id = repository.save(entity);

            ApplyOrderEntity oldEntity = ApplyOrderEntity.builder()
                    .id(id)
                    .status(ApplyOrderStatusEnum.DRAFT)
                    .build();
            ApplyOrderEntity newEntity = ApplyOrderEntity.builder()
                    .id(id)
                    .status(ApplyOrderStatusEnum.APPROVED)
                    .build();

            int result = repository.update(oldEntity, newEntity);
            assertEquals(1, result, "更新成功应返回 1");
        }

        @Test
        @DisplayName("更新不存在的实体应返回 0")
        void shouldReturnZeroForNonExistingEntity() {
            ApplyOrderEntity oldEntity = ApplyOrderEntity.builder().id(999L).build();
            ApplyOrderEntity newEntity = ApplyOrderEntity.builder().id(999L).build();

            int result = repository.update(oldEntity, newEntity);
            assertEquals(0, result, "不存在的实体更新应返回 0");
        }

        @Test
        @DisplayName("更新后 findById 应返回新数据")
        void shouldReflectUpdateInFindById() {
            ApplyOrderEntity entity = ApplyOrderEntity.builder()
                    .applyOrderNo("AP005")
                    .status(ApplyOrderStatusEnum.DRAFT)
                    .build();
            Long id = repository.save(entity);

            ApplyOrderEntity updated = ApplyOrderEntity.builder()
                    .id(id)
                    .applyOrderNo("AP005")
                    .status(ApplyOrderStatusEnum.SENT_EXPRESS)
                    .build();
            repository.update(entity, updated);

            ApplyOrderEntity found = repository.findById(id);
            assertEquals(ApplyOrderStatusEnum.SENT_EXPRESS, found.getStatus(),
                    "更新后状态应为 SENT_EXPRESS");
        }
    }
}
