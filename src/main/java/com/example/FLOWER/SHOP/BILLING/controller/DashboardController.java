package com.example.FLOWER.SHOP.BILLING.controller;

import com.example.FLOWER.SHOP.BILLING.dto.DashboardSummaryDto;
import com.example.FLOWER.SHOP.BILLING.model.Bill;
import com.example.FLOWER.SHOP.BILLING.model.PaymentStatus;
import com.example.FLOWER.SHOP.BILLING.repository.BillRepository;
import com.example.FLOWER.SHOP.BILLING.repository.CustomerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final BillRepository billRepository;
    private final CustomerRepository customerRepository;

    public DashboardController(BillRepository billRepository, CustomerRepository customerRepository) {
        this.billRepository = billRepository;
        this.customerRepository = customerRepository;
    }

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryDto> getSummary(@RequestParam(value = "month", required = false) String month) {
        List<Bill> bills = billRepository.findAll();

        if (month != null && !month.isBlank()) {
            try {
                java.time.YearMonth targetMonth = java.time.YearMonth.parse(month);
                bills = bills.stream()
                        .filter(b -> java.time.YearMonth.from(b.getBillDate()).equals(targetMonth))
                        .toList();
            } catch (Exception e) {
                // Ignore parse errors, fallback to all bills
            }
        }

        double totalSales = bills.stream().mapToDouble(Bill::getTotalAmount).sum();
        double pendingDues = bills.stream().mapToDouble(Bill::getDueAmount).sum();
        long paidBills = bills.stream().filter(b -> b.getStatus() == PaymentStatus.PAID).count();
        long partialBills = bills.stream().filter(b -> b.getStatus() == PaymentStatus.PARTIAL).count();
        long pendingBills = bills.stream().filter(b -> b.getStatus() == PaymentStatus.PENDING).count();

        DashboardSummaryDto summary = DashboardSummaryDto.builder()
                .totalCustomers(customerRepository.count())
                .totalSales(totalSales)
                .pendingDues(pendingDues)
                .paidBills(paidBills)
                .partialBills(partialBills)
                .pendingBills(pendingBills)
                .build();
        return ResponseEntity.ok(summary);
    }
}
