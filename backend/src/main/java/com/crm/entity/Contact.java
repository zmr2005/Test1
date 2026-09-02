package com.crm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 联系人。
 */
@Data
@TableName("contacts")
public class Contact {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long customerId;
    private String name;
    private String position;
    private String phone;
    private String email;
    private Boolean isDecisionMaker;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
