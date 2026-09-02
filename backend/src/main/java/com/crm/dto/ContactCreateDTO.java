package com.crm.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ContactCreateDTO {

    @NotBlank(message = "姓名不能为空")
    private String name;

    private String position;
    private String phone;
    private String email;
    private Boolean isDecisionMaker;
}
