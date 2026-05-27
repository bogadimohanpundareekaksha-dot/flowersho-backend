package com.example.FLOWER.SHOP.BILLING.repository;

import com.example.FLOWER.SHOP.BILLING.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByMobileNumber(String mobileNumber);
    List<Customer> findByNameContainingIgnoreCase(String name);
}
