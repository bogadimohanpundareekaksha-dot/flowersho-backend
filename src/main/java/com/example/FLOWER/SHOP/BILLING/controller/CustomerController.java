package com.example.FLOWER.SHOP.BILLING.controller;

import com.example.FLOWER.SHOP.BILLING.dto.CustomerBalanceDto;
import com.example.FLOWER.SHOP.BILLING.model.Bill;
import com.example.FLOWER.SHOP.BILLING.model.Customer;
import com.example.FLOWER.SHOP.BILLING.model.Payment;
import com.example.FLOWER.SHOP.BILLING.model.UploadedImage;
import com.example.FLOWER.SHOP.BILLING.repository.UploadedImageRepository;
import com.example.FLOWER.SHOP.BILLING.repository.PaymentRepository;
import com.example.FLOWER.SHOP.BILLING.service.BillService;
import com.example.FLOWER.SHOP.BILLING.service.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Comparator;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final BillService billService;
    private final UploadedImageRepository uploadedImageRepository;
    private final PaymentRepository paymentRepository;

    public CustomerController(CustomerService customerService,
                              BillService billService,
                              UploadedImageRepository uploadedImageRepository,
                              PaymentRepository paymentRepository) {
        this.customerService = customerService;
        this.billService = billService;
        this.uploadedImageRepository = uploadedImageRepository;
        this.paymentRepository = paymentRepository;
    }

    @PostMapping
    public ResponseEntity<Customer> createOrUpdateCustomer(@RequestBody Customer customer) {
        Customer existing = customerService.findByMobileNumber(customer.getMobileNumber()).orElse(null);
        if (existing != null) {
            existing.setName(customer.getName());
            existing.setAddress(customer.getAddress());
            existing.setNotes(customer.getNotes());
            return ResponseEntity.ok(customerService.save(existing));
        }
        return ResponseEntity.ok(customerService.save(customer));
    }

    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(customerService.findAll());
    }

    @GetMapping("/due")
    public ResponseEntity<List<CustomerBalanceDto>> getDueCustomers() {
        List<CustomerBalanceDto> dueCustomers = customerService.findAll().stream()
                .map(customer -> {
                    double dueAmount = billService.findByCustomer(customer).stream()
                            .mapToDouble(Bill::getDueAmount)
                            .sum();
                    return CustomerBalanceDto.builder()
                            .id(customer.getId())
                            .name(customer.getName())
                            .mobileNumber(customer.getMobileNumber())
                            .address(customer.getAddress())
                            .dueAmount(dueAmount)
                            .build();
                })
                .filter(customer -> customer.getDueAmount() > 0)
                .sorted(Comparator.comparing(CustomerBalanceDto::getDueAmount).reversed())
                .collect(Collectors.toList());

        return ResponseEntity.ok(dueCustomers);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchCustomer(@RequestParam("query") String query) {
        Customer customer = customerService.findByMobileNumber(query).orElse(null);
        if (customer == null) {
            List<Customer> byName = customerService.findByName(query);
            if (byName.isEmpty()) {
                throw new IllegalArgumentException("Customer not found");
            }
            if (byName.size() > 1) {
                throw new IllegalArgumentException("Multiple customers found with this name. Please use mobile number or select from All Customers.");
            }
            customer = byName.get(0);
        }
        
        List<Bill> bills = billService.findByCustomer(customer);
        List<UploadedImage> images = uploadedImageRepository.findByCustomerIdOrderByUploadedAtDesc(customer.getId());
        List<Payment> payments = paymentRepository.findByCustomerOrderByPaymentDateDesc(customer);
        Map<String, Object> response = new HashMap<>();
        response.put("customer", customer);
        response.put("bills", bills);
        response.put("images", images);
        response.put("recentPayments", payments);
        response.put("pendingBalance", bills.stream().mapToDouble(Bill::getDueAmount).sum());
        response.put("totalPaid", bills.stream().mapToDouble(Bill::getPaidAmount).sum());
        return ResponseEntity.ok(response);
    }
}
