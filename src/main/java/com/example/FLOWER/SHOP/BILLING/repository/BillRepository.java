package com.example.FLOWER.SHOP.BILLING.repository;

import com.example.FLOWER.SHOP.BILLING.model.Bill;
import com.example.FLOWER.SHOP.BILLING.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BillRepository extends JpaRepository<Bill, Long> {
    List<Bill> findByCustomerOrderByBillDateDesc(Customer customer);
}
