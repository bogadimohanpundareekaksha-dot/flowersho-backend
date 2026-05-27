package com.example.FLOWER.SHOP.BILLING.repository;

import com.example.FLOWER.SHOP.BILLING.model.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {
    Optional<AdminUser> findByMobileNumber(String mobileNumber);
}
