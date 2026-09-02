package com.crm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.crm.entity.Customer;

public interface CustomerService extends IService<Customer> {

    /** 公海认领：public -> private */
    void reclaim(Long customerId, Long operatorId);

    /** 回收进公海：private -> public */
    void recycle(Long customerId, Long operatorId);

    /** 移交：更换负责人 */
    void transfer(Long customerId, Long toOwnerId, Long operatorId, String remark);
}
