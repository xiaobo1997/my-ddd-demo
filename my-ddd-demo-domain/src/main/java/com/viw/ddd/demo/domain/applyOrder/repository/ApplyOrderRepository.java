package com.viw.ddd.demo.domain.applyOrder.repository;

import com.viw.ddd.demo.domain.applyOrder.entity.ApplyOrderEntity;

/**
 * @author xhb
 * @Date 2026/1/9
 * @Description :
 */

public interface ApplyOrderRepository {

    Long save(ApplyOrderEntity applyOrderEntity);

    ApplyOrderEntity findById(Long id);


    ApplyOrderEntity findById(Long id ,String type );

    int update(ApplyOrderEntity oldApplyOrderEntity, ApplyOrderEntity newApplyOrderEntity);



}
