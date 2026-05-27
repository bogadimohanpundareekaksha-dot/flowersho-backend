package com.example.FLOWER.SHOP.BILLING.controller;

import com.example.FLOWER.SHOP.BILLING.config.JwtTokenProvider;
import com.example.FLOWER.SHOP.BILLING.config.OtpService;
import com.example.FLOWER.SHOP.BILLING.config.SmsService;
import com.example.FLOWER.SHOP.BILLING.dto.AuthRequest;
import com.example.FLOWER.SHOP.BILLING.dto.AuthResponse;
import com.example.FLOWER.SHOP.BILLING.dto.OtpRequest;
import com.example.FLOWER.SHOP.BILLING.dto.OtpVerifyRequest;
import com.example.FLOWER.SHOP.BILLING.model.AdminUser;
import com.example.FLOWER.SHOP.BILLING.service.AdminUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final AdminUserService adminUserService;
    private final OtpService otpService;
    private final SmsService smsService;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtTokenProvider tokenProvider,
                          AdminUserService adminUserService,
                          OtpService otpService,
                          SmsService smsService) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.adminUserService = adminUserService;
        this.otpService = otpService;
        this.smsService = smsService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getMobileNumber(), authRequest.getPassword())
            );
            String token = tokenProvider.createToken(auth);
            AdminUser admin = adminUserService.findByMobileNumber(authRequest.getMobileNumber());
            return ResponseEntity.ok(new AuthResponse(token, admin.getName()));
        } catch (Exception ex) {
            throw new BadCredentialsException("Invalid mobile number or password");
        }
    }

    @PostMapping("/otp/request")
    public ResponseEntity<?> requestOtp(@RequestBody OtpRequest request) {
        AdminUser admin = adminUserService.findByMobileNumber(request.getMobileNumber());
        String otp = otpService.generateOtp(admin.getMobileNumber());
        smsService.sendOtp(admin.getMobileNumber(), admin.getName(), otp);
        return ResponseEntity.ok().body("OTP sent to registered mobile number");
    }

    @PostMapping("/otp/verify")
    public ResponseEntity<AuthResponse> verifyOtp(@RequestBody OtpVerifyRequest request) {
        boolean valid = otpService.verifyOtp(request.getMobileNumber(), request.getOtp());
        if (!valid) {
            throw new BadCredentialsException("Invalid or expired OTP");
        }
        AdminUser admin = adminUserService.findByMobileNumber(request.getMobileNumber());
        User user = new User(admin.getMobileNumber(), admin.getPassword(), admin.getRoles().contains("ADMIN") ? java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_ADMIN")) : java.util.List.of());
        Authentication auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        String token = tokenProvider.createToken(auth);
        return ResponseEntity.ok(new AuthResponse(token, admin.getName()));
    }
}
