package com.example.FLOWER.SHOP.BILLING.service;

import com.example.FLOWER.SHOP.BILLING.model.Bill;
import com.example.FLOWER.SHOP.BILLING.model.Customer;
import com.example.FLOWER.SHOP.BILLING.model.Payment;
import com.example.FLOWER.SHOP.BILLING.model.PaymentStatus;
import com.example.FLOWER.SHOP.BILLING.repository.BillRepository;
import com.example.FLOWER.SHOP.BILLING.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BillRepository billRepository;

    public PaymentService(PaymentRepository paymentRepository, BillRepository billRepository) {
        this.paymentRepository = paymentRepository;
        this.billRepository = billRepository;
    }

    public Payment addPayment(Bill bill, Customer customer, double amountPaid, LocalDate paymentDate, String paymentMode, String notes) {
        double newPaid = bill.getPaidAmount() + amountPaid;
        double newDue = Math.max(0, bill.getTotalAmount() - newPaid);
        bill.setPaidAmount(newPaid);
        bill.setDueAmount(newDue);
        bill.setStatus(determineStatus(newPaid, bill.getTotalAmount()));
        billRepository.save(bill);

        Payment payment = Payment.builder()
                .bill(bill)
                .customer(customer)
                .amountPaid(amountPaid)
                .paymentDate(paymentDate)
                .paymentMode(paymentMode)
                .notes(notes)
                .build();
        Payment saved = paymentRepository.save(payment);
        return saved;
    }

    public Payment updatePayment(Long paymentId, double amountPaid, LocalDate paymentDate, String paymentMode, String notes) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));
        payment.setAmountPaid(amountPaid);
        payment.setPaymentDate(paymentDate);
        payment.setPaymentMode(paymentMode);
        payment.setNotes(notes);
        paymentRepository.save(payment);

        Bill bill = payment.getBill();
        List<Payment> payments = paymentRepository.findByBillOrderByPaymentDateDesc(bill);
        double totalPaid = payments.stream().mapToDouble(Payment::getAmountPaid).sum();
        bill.setPaidAmount(totalPaid);
        bill.setDueAmount(Math.max(0, bill.getTotalAmount() - totalPaid));
        bill.setStatus(determineStatus(totalPaid, bill.getTotalAmount()));
        billRepository.save(bill);

        return payment;
    }

    public List<Payment> findPaymentsByBill(Bill bill) {
        return paymentRepository.findByBillOrderByPaymentDateDesc(bill);
    }

    private PaymentStatus determineStatus(double paidAmount, double totalAmount) {
        if (paidAmount <= 0) {
            return PaymentStatus.PENDING;
        }
        if (paidAmount >= totalAmount) {
            return PaymentStatus.PAID;
        }
        return PaymentStatus.PARTIAL;
    }
}
