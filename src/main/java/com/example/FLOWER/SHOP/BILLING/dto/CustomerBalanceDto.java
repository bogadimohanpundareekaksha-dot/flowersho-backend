package com.example.FLOWER.SHOP.BILLING.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerBalanceDto {
    private Long id;
    private String name;
    private String mobileNumber;
    private String address;
    private Double dueAmount;
}
