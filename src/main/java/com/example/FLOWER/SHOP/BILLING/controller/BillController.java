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
            @RequestParam("customerId") Long customerId,
            @RequestParam("totalAmount") Double totalAmount,
            @RequestParam("paidAmount") Double paidAmount,
            @RequestParam("billDate") String billDate,
            @RequestParam(value = "paymentMode", required = false) String paymentMode,
            @RequestParam(value = "notes", required = false) String notes,
            @RequestParam(value = "billImage", required = false) MultipartFile billImage) {
        Customer customer = customerService.findById(customerId);
        Bill result = billService.createBill(customer, totalAmount, paidAmount, paymentMode, LocalDate.parse(billDate), billImage, notes);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{billId}")
    public ResponseEntity<Void> updateBill(
            @PathVariable("billId") Long billId,
            @RequestParam("totalAmount") Double totalAmount,
            @RequestParam("paidAmount") Double paidAmount,
            @RequestParam("billDate") String billDate,
            @RequestParam(value = "notes", required = false) String notes,
            @RequestParam(value = "billImage", required = false) MultipartFile billImage) {
        billService.updateBill(billId, totalAmount, paidAmount, LocalDate.parse(billDate), notes, billImage);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Bill>> getBillsByCustomer(@PathVariable("customerId") Long customerId) {
        Customer customer = customerService.findById(customerId);
        return ResponseEntity.ok(billService.findByCustomer(customer));
    }
}
