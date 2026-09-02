package com.crm.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class LeadBatchAssignDTO {

    @NotEmpty(message = "线索ID列表不能为空")
    private List<Long> leadIds;

    @NotNull(message = "负责人不能为空")
    private Long ownerId;
}
