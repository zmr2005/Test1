package com.crm.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FollowUpLogDTO {

    @NotBlank(message = "记录类型不能为空")
    private String recordType;

    private Long opportunityId;
    private String content;
    private BigDecimal amount;
    private String attachment;
    private LocalDateTime nextFollowAt;
}
