package com.example.FLOWER.SHOP.BILLING.dto;

import lombok.Data;

@Data
public class CustomerDto {
    private Long id;
    private String name;
    private String mobileNumber;
    private String address;
    private String notes;
}
