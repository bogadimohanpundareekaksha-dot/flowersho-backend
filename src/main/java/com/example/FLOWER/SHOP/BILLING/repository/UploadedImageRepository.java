package com.example.FLOWER.SHOP.BILLING.repository;

import com.example.FLOWER.SHOP.BILLING.model.UploadedImage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UploadedImageRepository extends JpaRepository<UploadedImage, Long> {
    List<UploadedImage> findByCustomerIdOrderByUploadedAtDesc(Long customerId);
}
