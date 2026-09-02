package com.crm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 客户归属变更日志。
 */
@Data
@TableName("customer_assign_logs")
public class CustomerAssignLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long customerId;
    private Long fromOwnerId;
    private Long toOwnerId;
    private String action;
    private Long operatorId;
    private String remark;
    private LocalDateTime createdAt;
}
