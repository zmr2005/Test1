package com.crm.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class StageChangeDTO {

    @NotBlank(message = "目标阶段不能为空")
    private String stage;

    private String remark;
}
