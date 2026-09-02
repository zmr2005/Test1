package com.crm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商机阶段流转日志。
 */
@Data
@TableName("opportunity_stage_logs")
public class OpportunityStageLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long opportunityId;
    private String fromStage;
    private String toStage;
    private String remark;
    private Long operatorId;
    private LocalDateTime createdAt;
}
