package com.crm.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskCreateDTO {

    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "类型不能为空")
    private String type;

    private String content;
    private String relatedType;
    private Long relatedId;
    private LocalDateTime dueAt;
    private LocalDateTime remindAt;
}
