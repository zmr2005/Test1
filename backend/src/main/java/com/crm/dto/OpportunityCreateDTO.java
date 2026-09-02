package com.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class OpportunityCreateDTO {

    @NotNull(message = "客户ID不能为空")
    private Long customerId;

    @NotBlank(message = "商机名称不能为空")
    private String name;

    private String inquiry;
    private String product;
    private BigDecimal budget;
    private LocalDate expectedCloseDate;
    private Long ownerId;
    private String remark;
}
