package com.example.FLOWER.SHOP.BILLING.config;

public interface SmsService {
    void sendPaymentNotification(String mobileNumber, String customerName, double totalBill, double paidAmount, double remainingDue, String date);
    void sendOtp(String mobileNumber, String adminName, String otp);
}
