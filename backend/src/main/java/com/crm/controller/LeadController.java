package com.crm.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crm.common.BusinessException;
import com.crm.common.PageResult;
import com.crm.common.Result;
import com.crm.dto.LeadBatchAssignDTO;
import com.crm.dto.LeadCreateDTO;
import com.crm.dto.LeadUpdateDTO;
import com.crm.dto.DuplicateCheckDTO;
import com.crm.entity.Lead;
import com.crm.service.LeadService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/leads")
public class LeadController {

    @Resource
    private LeadService leadService;

    @GetMapping
    public Result<PageResult<Lead>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long sourceId,
            @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<Lead> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(status), Lead::getStatus, status)
                .eq(sourceId != null, Lead::getSourceId, sourceId)
                .and(StringUtils.hasText(keyword), w -> w
                        .like(Lead::getName, keyword).or().like(Lead::getCompany, keyword))
                .orderByDesc(Lead::getCreatedAt);
        Page<Lead> result = leadService.page(new Page<>(page, size), wrapper);
        return Result.ok(new PageResult<>(result.getRecords(), result.getTotal()));
    }

    @PostMapping
    public Result<Lead> create(@Valid @RequestBody LeadCreateDTO dto) {
        Lead lead = new Lead();
        BeanUtils.copyProperties(dto, lead);
        lead.setStatus(lead.getOwnerId() != null ? "assigned" : "pending");
        leadService.save(lead);
        return Result.ok(lead);
    }

    @GetMapping("/{id}")
    public Result<Lead> get(@PathVariable Long id) {
        Lead lead = leadService.getById(id);
        if (lead == null) {
            throw new BusinessException("线索不存在");
        }
        return Result.ok(lead);
    }

    @PutMapping("/{id}")
    public Result<Lead> update(@PathVariable Long id, @Valid @RequestBody LeadUpdateDTO dto) {
        Lead lead = leadService.getById(id);
        if (lead == null) {
            throw new BusinessException("线索不存在");
        }
        BeanUtils.copyProperties(dto, lead);
        leadService.updateById(lead);
        return Result.ok(lead);
    }

    @DeleteMapping("/{id}")
    public Result<Void> recycle(@PathVariable Long id) {
        leadService.recycle(id);
        return Result.ok();
    }

    @PostMapping("/{id}/restore")
    public Result<Void> restore(@PathVariable Long id) {
        leadService.restore(id);
        return Result.ok();
    }

    @DeleteMapping("/{id}/purge")
    public Result<Void> purge(@PathVariable Long id) {
        leadService.purge(id);
        return Result.ok();
    }

    @PostMapping("/batch-assign")
    public Result<Integer> batchAssign(@Valid @RequestBody LeadBatchAssignDTO dto) {
        int count = leadService.batchAssign(dto.getLeadIds(), dto.getOwnerId());
        return Result.ok(count);
    }

    @PostMapping("/duplicate-check")
    public Result<List<Lead>> duplicateCheck(@RequestBody DuplicateCheckDTO dto) {
        return Result.ok(leadService.duplicateCheck(dto.getPhone(), dto.getEmail(), dto.getCompany()));
    }
}
