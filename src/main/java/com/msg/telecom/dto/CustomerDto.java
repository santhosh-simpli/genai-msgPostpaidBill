package com.msg.telecom.dto;

import lombok.Data;

@Data
public class CustomerDto {
    private Long customerId;
    private String name;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    private Long userId;
    private UserDto user; // Include full user details for linked data display
}
