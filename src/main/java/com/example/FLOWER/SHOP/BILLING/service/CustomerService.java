package com.example.FLOWER.SHOP.BILLING.service;

import com.example.FLOWER.SHOP.BILLING.model.Customer;
import com.example.FLOWER.SHOP.BILLING.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer save(Customer customer) {
        return customerRepository.save(customer);
    }

    public Optional<Customer> findByMobileNumber(String mobileNumber) {
        return customerRepository.findByMobileNumber(mobileNumber);
    }

    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    public List<Customer> findByName(String name) {
        return customerRepository.findByNameContainingIgnoreCase(name);
    }

    public Customer findById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
    }
}
