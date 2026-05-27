package com.example.FLOWER.SHOP.BILLING.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String adminName;
}
