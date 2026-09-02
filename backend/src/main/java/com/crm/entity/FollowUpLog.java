package com.crm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 跟进记录（统一时间线）。
 */
@Data
@TableName("follow_up_logs")
public class FollowUpLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long customerId;
    private Long opportunityId;
    private String recordType;
    private String content;
    private BigDecimal amount;
    private String attachment;
    private LocalDateTime nextFollowAt;
    private Long operatorId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
