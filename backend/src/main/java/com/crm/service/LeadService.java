package com.crm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.crm.entity.Lead;

import java.util.List;

public interface LeadService extends IService<Lead> {

    /** 查重：phone/email 精确匹配，company 模糊匹配 */
    List<Lead> duplicateCheck(String phone, String email, String company);

    /** 批量分配负责人，状态置为已分配 */
    int batchAssign(List<Long> leadIds, Long ownerId);

    /** 移入回收站 */
    void recycle(Long id);

    /** 从回收站恢复 */
    void restore(Long id);

    /** 彻底删除 */
    void purge(Long id);
}
