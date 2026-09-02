package com.crm.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LeadCreateDTO {

    @NotBlank(message = "姓名不能为空")
    private String name;

    private String company;
    private String phone;

    @Email(message = "邮箱格式不正确")
    private String email;

    private Long sourceId;
    private Long ownerId;
    private String tags;
    private String remark;
}
