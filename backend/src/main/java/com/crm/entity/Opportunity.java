package com.crm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 商机。
 */
@Data
@TableName("opportunities")
public class Opportunity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long customerId;
    private String name;
    private String inquiry;
    private String product;
    private BigDecimal budget;
    private String stage;
    private LocalDate expectedCloseDate;
    private Long ownerId;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
