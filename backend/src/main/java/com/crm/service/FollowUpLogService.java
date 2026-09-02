package com.crm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.crm.dto.FollowUpLogDTO;
import com.crm.entity.FollowUpLog;

public interface FollowUpLogService extends IService<FollowUpLog> {

    /** 新增跟进日志并更新客户最近跟进时间 */
    FollowUpLog addLog(Long customerId, FollowUpLogDTO dto, Long operatorId);
}
