package com.crm.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.crm.common.Result;
import com.crm.dto.FollowUpLogDTO;
import com.crm.entity.FollowUpLog;
import com.crm.service.FollowUpLogService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customers/{customerId}/follow-ups")
public class FollowUpLogController {

    @Resource
    private FollowUpLogService followUpLogService;

    @GetMapping
    public Result<List<FollowUpLog>> list(@PathVariable Long customerId) {
        LambdaQueryWrapper<FollowUpLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FollowUpLog::getCustomerId, customerId)
                .orderByDesc(FollowUpLog::getCreatedAt);
        return Result.ok(followUpLogService.list(wrapper));
    }

    @PostMapping
    public Result<FollowUpLog> add(@PathVariable Long customerId,
                                   @Valid @RequestBody FollowUpLogDTO dto,
                                   @RequestHeader(value = "X-User-Id", defaultValue = "0") Long operatorId) {
        return Result.ok(followUpLogService.addLog(customerId, dto, operatorId));
    }
}
