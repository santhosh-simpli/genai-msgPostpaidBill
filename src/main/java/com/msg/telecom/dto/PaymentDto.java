package com.msg.telecom.dto;

import lombok.Data;

@Data
public class PaymentDto {
    private Long paymentId;
    private Long invoiceId;
    private Double amount;
    private String paymentDate;
    private String status;
}
