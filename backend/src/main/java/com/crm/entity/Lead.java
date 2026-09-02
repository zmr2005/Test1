package com.crm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 线索。
 */
@Data
@TableName("leads")
public class Lead {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;
    private String company;
    private String phone;
    private String email;
    private Long sourceId;
    private String status;
    private Long ownerId;
    private String remark;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
