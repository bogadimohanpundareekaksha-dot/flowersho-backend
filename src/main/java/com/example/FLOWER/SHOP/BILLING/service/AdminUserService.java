package com.example.FLOWER.SHOP.BILLING.service;

import com.example.FLOWER.SHOP.BILLING.model.AdminUser;
import com.example.FLOWER.SHOP.BILLING.repository.AdminUserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class AdminUserService implements UserDetailsService {

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserService(AdminUserRepository adminUserRepository, PasswordEncoder passwordEncoder) {
        this.adminUserRepository = adminUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void initDefaultAdmin() {
        adminUserRepository.findByMobileNumber("9346959420").ifPresentOrElse(admin -> {
            if (!passwordEncoder.matches("shop1234", admin.getPassword())) {
                admin.setPassword(passwordEncoder.encode("shop1234"));
                admin.setRoles("ADMIN");
                adminUserRepository.save(admin);
            }
        }, () -> {
            AdminUser admin = AdminUser.builder()
                    .name("Flower Shop Admin")
                    .mobileNumber("9346959420")
                    .password(passwordEncoder.encode("shop1234"))
                    .roles("ADMIN")
                    .createdAt(Instant.now())
                    .build();
            adminUserRepository.save(admin);
        });
    }

    public AdminUser findByMobileNumber(String mobileNumber) {
        return adminUserRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new IllegalArgumentException("Admin user not found"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AdminUser admin = adminUserRepository.findByMobileNumber(username)
                .orElseThrow(() -> new UsernameNotFoundException("Admin user not found"));
        return new User(admin.getMobileNumber(), admin.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }
}
