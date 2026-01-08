package com.msg.telecom.dto;

import lombok.Data;

@Data
public class InvoiceDto {
    private Long invoiceId;
    private Long customerId;
    private Double totalAmount;
    private String status;
    private String dueDate;
    private String billingPeriodStart;
    private String billingPeriodEnd;
    private String billingStartDate; // Alternative name for frontend compatibility
    private String billingEndDate; // Alternative name for frontend compatibility
}
