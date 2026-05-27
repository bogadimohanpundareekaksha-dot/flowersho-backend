package com.example.FLOWER.SHOP.BILLING.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class BillDto {
    private Long customerId;
    private Double totalAmount;
    private Double paidAmount;
    private Double dueAmount;
    private LocalDate billDate;
    private String notes;
}
