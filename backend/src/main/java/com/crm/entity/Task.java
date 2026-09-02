package com.crm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 任务 / 日报 / 待办。
 */
@Data
@TableName("tasks")
public class Task {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;
    private String type;
    private String content;
    private Long ownerId;
    private String relatedType;
    private Long relatedId;
    private LocalDateTime dueAt;
    private LocalDateTime remindAt;
    private Boolean done;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
