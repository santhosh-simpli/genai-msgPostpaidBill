package com.msg.telecom.dto;

import lombok.Data;

@Data
public class UsageRecordDto {
    private Long usageId;
    private Long serviceId;
    private String usageType;
    private Double amount;
    private String usageDate;
}
