package com.example.FLOWER.SHOP.BILLING.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {

    private final Map<String, OtpEntry> otpStore = new ConcurrentHashMap<>();
    private final int expirationSeconds;
    private final Random random = new Random();

    public OtpService(@Value("${otp.expiration-seconds:300}") int expirationSeconds) {
        this.expirationSeconds = expirationSeconds;
    }

    public String generateOtp(String mobileNumber) {
        String otp = String.format("%06d", random.nextInt(900000) + 100000);
        otpStore.put(mobileNumber, new OtpEntry(otp, Instant.now().plusSeconds(expirationSeconds)));
        return otp;
    }

    public boolean verifyOtp(String mobileNumber, String otp) {
        OtpEntry entry = otpStore.get(mobileNumber);
        if (entry == null) {
            return false;
        }
        if (entry.expiresAt().isBefore(Instant.now())) {
            otpStore.remove(mobileNumber);
            return false;
        }
        boolean valid = entry.code().equals(otp);
        if (valid) {
            otpStore.remove(mobileNumber);
        }
        return valid;
    }

    private record OtpEntry(String code, Instant expiresAt) {}
}
