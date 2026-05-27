package com.example.FLOWER.SHOP.BILLING.dto;

import lombok.Data;

@Data
public class OtpVerifyRequest {
    private String mobileNumber;
    private String otp;
}
