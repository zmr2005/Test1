package com.crm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 客户。
 */
@Data
@TableName("customers")
public class Customer {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;
    private String industry;
    private String phone;
    private String email;
    private String status;
    private Long ownerId;
    private Long leadId;
    private LocalDateTime lastFollowUpAt;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
