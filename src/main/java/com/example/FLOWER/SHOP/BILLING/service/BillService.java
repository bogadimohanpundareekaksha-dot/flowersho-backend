package com.example.FLOWER.SHOP.BILLING.service;

import com.example.FLOWER.SHOP.BILLING.model.Bill;
import com.example.FLOWER.SHOP.BILLING.model.Customer;
import com.example.FLOWER.SHOP.BILLING.model.Payment;
import com.example.FLOWER.SHOP.BILLING.model.PaymentStatus;
import com.example.FLOWER.SHOP.BILLING.model.UploadedImage;
import com.example.FLOWER.SHOP.BILLING.repository.BillRepository;
import com.example.FLOWER.SHOP.BILLING.repository.PaymentRepository;
import com.example.FLOWER.SHOP.BILLING.repository.UploadedImageRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Service
public class BillService {

    private final BillRepository billRepository;
    private final PaymentRepository paymentRepository;
    private final UploadedImageRepository uploadedImageRepository;
    private final FileStorageService fileStorageService;

    public BillService(BillRepository billRepository,
                       PaymentRepository paymentRepository,
                       UploadedImageRepository uploadedImageRepository,
                       FileStorageService fileStorageService) {
        this.billRepository = billRepository;
        this.paymentRepository = paymentRepository;
        this.uploadedImageRepository = uploadedImageRepository;
        this.fileStorageService = fileStorageService;
    }

    public Bill createBill(Customer customer, double totalAmount, double paidAmount, String paymentMode, LocalDate billDate, MultipartFile billFile, String notes) {
        double dueAmount = totalAmount - paidAmount;
        PaymentStatus status = calculateStatus(paidAmount, totalAmount);
        Bill bill = Bill.builder()
                .customer(customer)
                .totalAmount(totalAmount)
                .paidAmount(paidAmount)
                .dueAmount(dueAmount)
                .billDate(billDate)
                .status(status)
                .notes(notes)
                .build();
        String filename = fileStorageService.storeFile(billFile);
        if (filename != null) {
            bill.setBillImageUrl(filename);
        }
        Bill saved = billRepository.save(bill);
        if (filename != null) {
            uploadedImageRepository.save(UploadedImage.builder()
                    .customer(customer)
                    .bill(saved)
                    .filename(filename)
                    .url(filename)
                    .description("Bill image upload")
                    .build());
        }

        if (paidAmount > 0) {
            Payment payment = Payment.builder()
                    .bill(saved)
                    .customer(customer)
                    .amountPaid(paidAmount)
                    .paymentDate(billDate)
                    .paymentMode(paymentMode != null && !paymentMode.isBlank() ? paymentMode : "Cash")
                    .notes("Initial payment during bill creation")
                    .build();
            paymentRepository.save(payment);
        }

        return saved;
    }

    public Bill updateBill(Long billId, double totalAmount, double paidAmount, LocalDate billDate, String notes, MultipartFile billFile) {
        Bill saved = billRepository.findById(billId)
                .orElseThrow(() -> new IllegalArgumentException("Bill not found"));
        double dueAmount = Math.max(0, totalAmount - paidAmount);
        saved.setTotalAmount(totalAmount);
        saved.setPaidAmount(paidAmount);
        saved.setDueAmount(dueAmount);
        saved.setBillDate(billDate);
        saved.setNotes(notes);
        saved.setStatus(calculateStatus(paidAmount, totalAmount));

        String filename = fileStorageService.storeFile(billFile);
        if (filename != null) {
            saved.setBillImageUrl(filename);
            List<UploadedImage> existingImages = uploadedImageRepository.findByBillIdOrderByUploadedAtDesc(saved.getId());
            UploadedImage image = existingImages.isEmpty()
                    ? UploadedImage.builder()
                    .customer(saved.getCustomer())
                    .bill(saved)
                    .description("Bill image upload")
                    .build()
                    : existingImages.get(0);
            image.setFilename(filename);
            image.setUrl(filename);
            image.setCustomer(saved.getCustomer());
            image.setBill(saved);
            uploadedImageRepository.save(image);
        }

        return billRepository.save(saved);
    }

    public List<Bill> findByCustomer(Customer customer) {
        return billRepository.findByCustomerOrderByBillDateDesc(customer);
    }

    public Bill findById(Long id) {
        return billRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bill not found"));
    }

    private PaymentStatus calculateStatus(double paidAmount, double totalAmount) {
        if (paidAmount <= 0) {
            return PaymentStatus.PENDING;
        }
        if (paidAmount >= totalAmount) {
            return PaymentStatus.PAID;
        }
        return PaymentStatus.PARTIAL;
    }
}
