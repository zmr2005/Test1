package com.crm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.crm.entity.Opportunity;

public interface OpportunityService extends IService<Opportunity> {

    /** 商机阶段流转，记录流转日志 */
    void changeStage(Long opportunityId, String stage, String remark, Long operatorId);
}
