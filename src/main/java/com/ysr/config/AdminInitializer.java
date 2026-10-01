package com.ysr.config;

import com.ysr.model.Users;
import com.ysr.repository.UserRepo;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer {

    private UserRepo userRepo;
    private PasswordEncoder passwordEncoder;

    @Value("${admin.name}")
    private String name;

    @Value("${admin.email}")
    private String email;

    @Value("${admin.phone}")
    private String phone;

    @Value("${admin.password}")
    private String password;

    public AdminInitializer(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void createAdmin() {
        if (userRepo.existsByEmail(email)) {
            return;
        }
        Users admin = new Users();
        admin.setName(name);
        admin.setEmail(email);
        admin.setPhone(phone);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setRole("ROLE_ADMIN");
        userRepo.save(admin);
    }

}
