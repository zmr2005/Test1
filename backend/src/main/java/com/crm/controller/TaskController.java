package com.crm.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crm.common.BusinessException;
import com.crm.common.PageResult;
import com.crm.common.Result;
import com.crm.dto.TaskCreateDTO;
import com.crm.entity.Task;
import com.crm.service.TaskService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Resource
    private TaskService taskService;

    @GetMapping
    public Result<PageResult<Task>> list(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Long ownerId) {
        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(type), Task::getType, type)
                .eq(ownerId != null, Task::getOwnerId, ownerId)
                .orderByDesc(Task::getCreatedAt);
        Page<Task> result = taskService.page(new Page<>(page, size), wrapper);
        return Result.ok(new PageResult<>(result.getRecords(), result.getTotal()));
    }

    @PostMapping
    public Result<Task> create(@Valid @RequestBody TaskCreateDTO dto,
                               @RequestHeader(value = "X-User-Id", defaultValue = "0") Long operatorId) {
        Task task = new Task();
        BeanUtils.copyProperties(dto, task);
        task.setOwnerId(operatorId);
        task.setDone(false);
        taskService.save(task);
        return Result.ok(task);
    }

    @PutMapping("/{id}/done")
    public Result<Task> toggleDone(@PathVariable Long id, @RequestParam boolean done) {
        Task task = taskService.getById(id);
        if (task == null) {
            throw new BusinessException("任务不存在");
        }
        task.setDone(done);
        taskService.updateById(task);
        return Result.ok(task);
    }
}
