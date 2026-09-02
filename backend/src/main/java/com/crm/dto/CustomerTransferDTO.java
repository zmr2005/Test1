package com.crm.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CustomerTransferDTO {

    @NotNull(message = "目标负责人不能为空")
    private Long toOwnerId;

    private String remark;
}
