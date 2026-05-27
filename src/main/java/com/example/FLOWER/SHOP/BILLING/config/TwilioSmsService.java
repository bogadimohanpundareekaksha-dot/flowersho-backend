package com.example.FLOWER.SHOP.BILLING.config;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TwilioSmsService implements SmsService {

    private final String fromNumber;
    private final boolean enabled;

    public TwilioSmsService(@Value("${twilio.account-sid:}") String accountSid,
                            @Value("${twilio.auth-token:}") String authToken,
                            @Value("${twilio.from-number:}") String fromNumber) {
        this.fromNumber = fromNumber;
        this.enabled = accountSid != null && !accountSid.isBlank() && authToken != null && !authToken.isBlank() && fromNumber != null && !fromNumber.isBlank();
        if (enabled) {
            Twilio.init(accountSid, authToken);
        }
    }

    @Override
    public void sendPaymentNotification(String mobileNumber, String customerName, double totalBill, double paidAmount, double remainingDue, String date) {
        String body = String.format("Hello %s, your flower bill is ₹%.2f. You paid ₹%.2f. Remaining due is ₹%.2f. Date: %s. Thank you.", customerName, totalBill, paidAmount, remainingDue, date);
        sendMessage(mobileNumber, body);
    }

    @Override
    public void sendOtp(String mobileNumber, String adminName, String otp) {
        String body = String.format("Hello %s, your OTP for Flower Shop Billing login is %s. It expires in 5 minutes.", adminName, otp);
        sendMessage(mobileNumber, body);
    }

    private void sendMessage(String mobileNumber, String body) {
        if (!enabled) {
            System.out.println("[SMS Mock] " + body + " -> " + mobileNumber);
            return;
        }

        Message.creator(
                new PhoneNumber(mobileNumber),
                new PhoneNumber(fromNumber),
                body
        ).create();
    }
}
