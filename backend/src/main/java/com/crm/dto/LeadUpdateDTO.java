package com.crm.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class LeadUpdateDTO {

    private String name;
    private String company;
    private String phone;

    @Email(message = "邮箱格式不正确")
    private String email;

    private Long sourceId;
    private String status;
    private Long ownerId;
    private String tags;
    private String remark;
}
