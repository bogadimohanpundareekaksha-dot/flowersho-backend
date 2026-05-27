package com.example.FLOWER.SHOP.BILLING.controller;

import com.example.FLOWER.SHOP.BILLING.model.Bill;
import com.example.FLOWER.SHOP.BILLING.model.Customer;
import com.example.FLOWER.SHOP.BILLING.model.Payment;
import com.example.FLOWER.SHOP.BILLING.service.BillService;
import com.example.FLOWER.SHOP.BILLING.service.CustomerService;
import com.example.FLOWER.SHOP.BILLING.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final BillService billService;
    private final CustomerService customerService;

    public PaymentController(PaymentService paymentService, BillService billService, CustomerService customerService) {
        this.paymentService = paymentService;
        this.billService = billService;
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<Payment> addPayment(@RequestBody com.example.FLOWER.SHOP.BILLING.dto.PaymentDto dto) {
        Bill bill = billService.findByCustomer(customerService.findById(dto.getCustomerId())).stream()
                .filter(b -> b.getId().equals(dto.getBillId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Bill not found for customer"));
        Customer customer = customerService.findById(dto.getCustomerId());
        Payment payment = paymentService.addPayment(bill, customer, dto.getAmountPaid(), dto.getPaymentDate(), dto.getPaymentMode(), dto.getNotes());
        return ResponseEntity.ok(payment);
    }

    @PutMapping("/{paymentId}")
    public ResponseEntity<Payment> updatePayment(@PathVariable Long paymentId, @RequestBody com.example.FLOWER.SHOP.BILLING.dto.PaymentDto dto) {
        Payment updated = paymentService.updatePayment(paymentId, dto.getAmountPaid(), dto.getPaymentDate(), dto.getPaymentMode(), dto.getNotes());
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/bill/{billId}")
    public ResponseEntity<List<Payment>> getPaymentsForBill(@PathVariable Long billId) {
        Bill bill = billService.findById(billId);
        return ResponseEntity.ok(paymentService.findPaymentsByBill(bill));
    }
}
