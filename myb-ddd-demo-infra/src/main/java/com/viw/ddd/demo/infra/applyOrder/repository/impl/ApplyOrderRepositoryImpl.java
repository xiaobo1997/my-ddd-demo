package com.viw.ddd.demo.infra.applyOrder.repository.impl;

import com.viw.ddd.demo.domain.applyOrder.entity.ApplyOrderEntity;
import com.viw.ddd.demo.domain.applyOrder.repository.ApplyOrderRepository;

/**
 * @author xhb
 * @Date 2026/1/9
 * @Description :
 */

public class ApplyOrderRepositoryImpl implements ApplyOrderRepository {
    @Override
    public Long save(ApplyOrderEntity applyOrderEntity) {
        // entity -> do

        //保存到数据库
        return 0L;
    }

    @Override
    public ApplyOrderEntity findById(Long id) {

        //从数据库查询
        return null;
    }

    @Override
    public ApplyOrderEntity findById(Long id, String type) {
        if("detail".equals(type)){
            //只查询快递信息
        }
        return null;
    }

    @Override
    public int update(ApplyOrderEntity oldApplyOrderEntity, ApplyOrderEntity newApplyOrderEntity) {
        // todo  领域模型实体持久化的工具类
        //可以通过工具，直接对比两个entity都差异，生成更新sql
        return 0;
    }
}
