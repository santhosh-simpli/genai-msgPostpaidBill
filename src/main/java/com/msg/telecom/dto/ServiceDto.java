package com.msg.telecom.dto;

import lombok.Data;

@Data
public class ServiceDto {
    private Long serviceId;
    private String serviceType;
    private String status;
    private Long customerId;
}
