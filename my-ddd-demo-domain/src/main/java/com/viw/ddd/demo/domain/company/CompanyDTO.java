package com.viw.ddd.demo.domain.company;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 【DDD - 领域层（Domain）· 外部数据值对象（Gateway 返回类型）】
 *
 * 用于承载 Gateway 从外部系统查询回来的数据。
 * 定义在领域层而非基础设施层，是因为：
 *   1. 领域层需要定义"我需要什么数据"的契约
 *   2. 防腐层（Anti-Corruption Layer）的一部分——隔离外部模型
 *   3. 应用层通过领域层的 Gateway 接口获取此对象，不应感知外部系统结构
 *
 * 注意：如果外部数据需要参与复杂业务逻辑，应转换为领域层的 VO/Entity，
 *       此 DTO 仅作为传输载体。
 *
 * @author xhb
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompanyDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 公司ID */
    private Long companyId;

    // 实际项目中可扩展更多字段，如：
    // private String companyName;
    // private String legalPerson;
    // private Integer status;
}
