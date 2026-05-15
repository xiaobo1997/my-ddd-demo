package com.viw.ddd.demo.app.applyOrder.assembler;

import com.viw.ddd.demo.api.applyOrder.dto.SubmitApplyOrderCommand;
import com.viw.ddd.demo.app.applyOrder.dto.SubmitApplyOrderDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 【DDD - 应用层（Application）· MapStruct 对象映射器（防腐层 Assembler）】
 *
 * 职责：将 api 层的 Command（外部契约）映射为 app 层的 DTO（内部模型）。
 *
 * 为什么用 MapStruct？
 *   1. 编译期生成实现类（性能优于反射-based 的 BeanUtils）
 *   2. 字段名一致时零配置，不一致时 @Mapping 标注
 *   3. 编译期就能发现映射错误
 *
 * 面试要点：
 *   "防腐层通过 MapStruct 隔离外部模型和内部模型，
 *    这是 CQRS + ACL（Anti-Corruption Layer）的实现方式之一。"
 *
 * @author xhb
 */
@Mapper(componentModel = "spring")
public interface ApplyOrderAssembler {

    /** 静态实例（非 Spring 环境下使用），Spring 环境下通过 @Autowired 注入 */
    ApplyOrderAssembler INSTANCE = Mappers.getMapper(ApplyOrderAssembler.class);

    /**
     * Command → DTO 转换
     * 字段名一致时 MapStruct 自动映射，无需额外配置
     */
    SubmitApplyOrderDTO toDTO(SubmitApplyOrderCommand command);
}
