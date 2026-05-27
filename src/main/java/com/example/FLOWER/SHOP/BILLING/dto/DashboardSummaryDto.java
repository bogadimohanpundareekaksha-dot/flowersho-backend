package com.example.FLOWER.SHOP.BILLING.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardSummaryDto {
    private long totalCustomers;
    private double totalSales;
    private double pendingDues;
    private long paidBills;
    private long partialBills;
    private long pendingBills;
}
