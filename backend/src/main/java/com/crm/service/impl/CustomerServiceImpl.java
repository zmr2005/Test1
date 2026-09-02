package com.crm.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crm.common.BusinessException;
import com.crm.entity.Customer;
import com.crm.entity.CustomerAssignLog;
import com.crm.mapper.CustomerAssignLogMapper;
import com.crm.mapper.CustomerMapper;
import com.crm.service.CustomerService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements CustomerService {

    @Resource
    private CustomerAssignLogMapper assignLogMapper;

    @Override
    @Transactional
    public void reclaim(Long customerId, Long operatorId) {
        Customer customer = getById(customerId);
        if (customer == null) {
            throw new BusinessException("客户不存在");
        }
        if (!"public".equals(customer.getStatus())) {
            throw new BusinessException("仅公海客户可认领");
        }
        Long fromOwner = customer.getOwnerId();
        // 原子更新，防止并发认领
        LambdaUpdateWrapper<Customer> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Customer::getId, customerId)
                .eq(Customer::getStatus, "public")
                .set(Customer::getStatus, "private")
                .set(Customer::getOwnerId, operatorId);
        if (!update(wrapper)) {
            throw new BusinessException("认领失败，客户已被他人认领");
        }
        saveAssignLog(customerId, fromOwner, operatorId, "reclaim", operatorId, null);
    }

    @Override
    @Transactional
    public void recycle(Long customerId, Long operatorId) {
        Customer customer = getById(customerId);
        if (customer == null) {
            throw new BusinessException("客户不存在");
        }
        if ("public".equals(customer.getStatus())) {
            throw new BusinessException("客户已在公海");
        }
        Long fromOwner = customer.getOwnerId();
        customer.setStatus("public");
        customer.setOwnerId(null);
        updateById(customer);
        saveAssignLog(customerId, fromOwner, null, "recycle", operatorId, null);
    }

    @Override
    @Transactional
    public void transfer(Long customerId, Long toOwnerId, Long operatorId, String remark) {
        Customer customer = getById(customerId);
        if (customer == null) {
            throw new BusinessException("客户不存在");
        }
        Long fromOwner = customer.getOwnerId();
        customer.setOwnerId(toOwnerId);
        customer.setStatus("private");
        updateById(customer);
        saveAssignLog(customerId, fromOwner, toOwnerId, "transfer", operatorId, remark);
    }

    private void saveAssignLog(Long customerId, Long fromOwnerId, Long toOwnerId,
                               String action, Long operatorId, String remark) {
        CustomerAssignLog log = new CustomerAssignLog();
        log.setCustomerId(customerId);
        log.setFromOwnerId(fromOwnerId);
        log.setToOwnerId(toOwnerId);
        log.setAction(action);
        log.setOperatorId(operatorId);
        log.setRemark(remark);
        assignLogMapper.insert(log);
    }
}
