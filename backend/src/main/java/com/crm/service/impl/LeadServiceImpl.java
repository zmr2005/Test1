package com.crm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crm.common.BusinessException;
import com.crm.entity.Lead;
import com.crm.mapper.LeadMapper;
import com.crm.service.LeadService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class LeadServiceImpl extends ServiceImpl<LeadMapper, Lead> implements LeadService {

    @Override
    public List<Lead> duplicateCheck(String phone, String email, String company) {
        if (!StringUtils.hasText(phone) && !StringUtils.hasText(email) && !StringUtils.hasText(company)) {
            throw new BusinessException("请至少提供一个查重维度");
        }
        Map<Long, Lead> dedup = new LinkedHashMap<>();
        if (StringUtils.hasText(phone)) {
            list(new LambdaQueryWrapper<Lead>()
                    .eq(Lead::getPhone, phone).ne(Lead::getStatus, "recycled"))
                    .forEach(l -> dedup.putIfAbsent(l.getId(), l));
        }
        if (StringUtils.hasText(email)) {
            list(new LambdaQueryWrapper<Lead>()
                    .eq(Lead::getEmail, email).ne(Lead::getStatus, "recycled"))
                    .forEach(l -> dedup.putIfAbsent(l.getId(), l));
        }
        if (StringUtils.hasText(company)) {
            list(new LambdaQueryWrapper<Lead>()
                    .like(Lead::getCompany, company).ne(Lead::getStatus, "recycled"))
                    .forEach(l -> dedup.putIfAbsent(l.getId(), l));
        }
        return new ArrayList<>(dedup.values());
    }

    @Override
    public int batchAssign(List<Long> leadIds, Long ownerId) {
        LambdaUpdateWrapper<Lead> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(Lead::getId, leadIds)
                .set(Lead::getOwnerId, ownerId)
                .set(Lead::getStatus, "assigned");
        return baseMapper.update(null, wrapper);
    }

    @Override
    public void recycle(Long id) {
        Lead lead = getById(id);
        if (lead == null) {
            throw new BusinessException("线索不存在");
        }
        lead.setStatus("recycled");
        updateById(lead);
    }

    @Override
    public void restore(Long id) {
        Lead lead = getById(id);
        if (lead == null) {
            throw new BusinessException("线索不存在");
        }
        if (!"recycled".equals(lead.getStatus())) {
            throw new BusinessException("仅回收站线索可恢复");
        }
        lead.setStatus(lead.getOwnerId() == null ? "pending" : "assigned");
        updateById(lead);
    }

    @Override
    public void purge(Long id) {
        Lead lead = getById(id);
        if (lead == null) {
            throw new BusinessException("线索不存在");
        }
        if (!"recycled".equals(lead.getStatus())) {
            throw new BusinessException("仅回收站线索可彻底删除");
        }
        removeById(id);
    }
}
