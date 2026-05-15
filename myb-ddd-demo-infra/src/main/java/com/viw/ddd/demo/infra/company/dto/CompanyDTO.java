package com.viw.ddd.demo.infra.company.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 【DDD - 基础设施层（Infrastructure）· 外部数据传输对象（External DTO）】
 *
 * 用于承载外部系统返回的数据。
 * 注意：这是对外部系统数据的"翻译层"，不是领域层的值对象。
 * 如果外部系统返回的数据需要参与业务逻辑，应在 domain 层定义对应的 VO。
 *
 * @author xhb
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompanyDTO implements Serializable {

    /** 公司ID */
    private Long companyId;
}
