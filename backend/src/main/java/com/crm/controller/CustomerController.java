package com.crm.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crm.common.BusinessException;
import com.crm.common.PageResult;
import com.crm.common.Result;
import com.crm.dto.CustomerTransferDTO;
import com.crm.entity.Customer;
import com.crm.service.CustomerService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Resource
    private CustomerService customerService;

    @GetMapping
    public Result<PageResult<Customer>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(status), Customer::getStatus, status)
                .and(StringUtils.hasText(keyword), w -> w
                        .like(Customer::getName, keyword).or().like(Customer::getPhone, keyword))
                .orderByDesc(Customer::getCreatedAt);
        Page<Customer> result = customerService.page(new Page<>(page, size), wrapper);
        return Result.ok(new PageResult<>(result.getRecords(), result.getTotal()));
    }

    @GetMapping("/{id}")
    public Result<Customer> get(@PathVariable Long id) {
        Customer customer = customerService.getById(id);
        if (customer == null) {
            throw new BusinessException("客户不存在");
        }
        return Result.ok(customer);
    }

    @PostMapping("/{id}/reclaim")
    public Result<Void> reclaim(@PathVariable Long id,
                                @RequestHeader(value = "X-User-Id", defaultValue = "0") Long operatorId) {
        customerService.reclaim(id, operatorId);
        return Result.ok();
    }

    @PostMapping("/{id}/recycle")
    public Result<Void> recycle(@PathVariable Long id,
                                @RequestHeader(value = "X-User-Id", defaultValue = "0") Long operatorId) {
        customerService.recycle(id, operatorId);
        return Result.ok();
    }

    @PostMapping("/{id}/transfer")
    public Result<Void> transfer(@PathVariable Long id,
                                 @Valid @RequestBody CustomerTransferDTO dto,
                                 @RequestHeader(value = "X-User-Id", defaultValue = "0") Long operatorId) {
        customerService.transfer(id, dto.getToOwnerId(), operatorId, dto.getRemark());
        return Result.ok();
    }
}
