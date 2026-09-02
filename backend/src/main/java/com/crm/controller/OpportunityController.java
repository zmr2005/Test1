package com.crm.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crm.common.BusinessException;
import com.crm.common.PageResult;
import com.crm.common.Result;
import com.crm.dto.OpportunityCreateDTO;
import com.crm.dto.StageChangeDTO;
import com.crm.entity.Opportunity;
import com.crm.service.OpportunityService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/opportunities")
public class OpportunityController {

    @Resource
    private OpportunityService opportunityService;

    @GetMapping
    public Result<PageResult<Opportunity>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String stage,
            @RequestParam(required = false) Long customerId) {
        LambdaQueryWrapper<Opportunity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(stage), Opportunity::getStage, stage)
                .eq(customerId != null, Opportunity::getCustomerId, customerId)
                .orderByDesc(Opportunity::getCreatedAt);
        Page<Opportunity> result = opportunityService.page(new Page<>(page, size), wrapper);
        return Result.ok(new PageResult<>(result.getRecords(), result.getTotal()));
    }

    @PostMapping
    public Result<Opportunity> create(@Valid @RequestBody OpportunityCreateDTO dto) {
        Opportunity opp = new Opportunity();
        BeanUtils.copyProperties(dto, opp);
        opp.setStage("contact");
        opportunityService.save(opp);
        return Result.ok(opp);
    }

    @GetMapping("/{id}")
    public Result<Opportunity> get(@PathVariable Long id) {
        Opportunity opp = opportunityService.getById(id);
        if (opp == null) {
            throw new BusinessException("商机不存在");
        }
        return Result.ok(opp);
    }

    @PutMapping("/{id}/stage")
    public Result<Void> changeStage(@PathVariable Long id,
                                    @Valid @RequestBody StageChangeDTO dto,
                                    @RequestHeader(value = "X-User-Id", defaultValue = "0") Long operatorId) {
        opportunityService.changeStage(id, dto.getStage(), dto.getRemark(), operatorId);
        return Result.ok();
    }
}
