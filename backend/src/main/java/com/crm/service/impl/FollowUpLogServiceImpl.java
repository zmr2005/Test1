package com.crm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crm.common.BusinessException;
import com.crm.dto.FollowUpLogDTO;
import com.crm.entity.Customer;
import com.crm.entity.FollowUpLog;
import com.crm.mapper.CustomerMapper;
import com.crm.mapper.FollowUpLogMapper;
import com.crm.service.FollowUpLogService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class FollowUpLogServiceImpl extends ServiceImpl<FollowUpLogMapper, FollowUpLog>
        implements FollowUpLogService {

    @Resource
    private CustomerMapper customerMapper;

    @Override
    @Transactional
    public FollowUpLog addLog(Long customerId, FollowUpLogDTO dto, Long operatorId) {
        Customer customer = customerMapper.selectById(customerId);
        if (customer == null) {
            throw new BusinessException("客户不存在");
        }
        FollowUpLog log = new FollowUpLog();
        log.setCustomerId(customerId);
        log.setOpportunityId(dto.getOpportunityId());
        log.setRecordType(dto.getRecordType());
        log.setContent(dto.getContent());
        log.setAmount(dto.getAmount());
        log.setAttachment(dto.getAttachment());
        log.setNextFollowAt(dto.getNextFollowAt());
        log.setOperatorId(operatorId);
        save(log);

        customer.setLastFollowUpAt(LocalDateTime.now());
        customerMapper.updateById(customer);
        return log;
    }
}
