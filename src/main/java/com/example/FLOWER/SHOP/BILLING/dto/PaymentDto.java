package com.example.FLOWER.SHOP.BILLING.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PaymentDto {
    private Long billId;
    private Long customerId;
    private Double amountPaid;
    private LocalDate paymentDate;
    private String paymentMode;
    private String notes;
}
