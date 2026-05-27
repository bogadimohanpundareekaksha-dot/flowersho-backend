package com.example.FLOWER.SHOP.BILLING.controller;

import com.example.FLOWER.SHOP.BILLING.model.Bill;
import com.example.FLOWER.SHOP.BILLING.model.Customer;
import com.example.FLOWER.SHOP.BILLING.service.BillService;
import com.example.FLOWER.SHOP.BILLING.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/bills")
public class BillController {

    private final BillService billService;
    private final CustomerService customerService;

    public BillController(BillService billService, CustomerService customerService) {
        this.billService = billService;
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<Bill> createBill(
            @RequestParam Long customerId,
            @RequestParam Double totalAmount,
            @RequestParam Double paidAmount,
            @RequestParam String billDate,
            @RequestParam(required = false) String paymentMode,
            @RequestParam(required = false) String notes,
            @RequestParam(required = false) MultipartFile billImage) {
        Customer customer = customerService.findById(customerId);
        Bill result = billService.createBill(customer, totalAmount, paidAmount, paymentMode, LocalDate.parse(billDate), billImage, notes);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{billId}")
    public ResponseEntity<Bill> updateBill(
            @PathVariable Long billId,
            @RequestParam Double paidAmount,
            @RequestParam Double dueAmount,
            @RequestParam String billDate,
            @RequestParam(required = false) String notes) {
        Bill result = billService.updateBill(billId, paidAmount, dueAmount, LocalDate.parse(billDate), notes);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Bill>> getBillsByCustomer(@PathVariable Long customerId) {
        Customer customer = customerService.findById(customerId);
        return ResponseEntity.ok(billService.findByCustomer(customer));
    }
}
