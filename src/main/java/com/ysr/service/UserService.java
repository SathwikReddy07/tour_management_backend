package com.ysr.service;

import com.ysr.model.Users;
import com.ysr.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private UserRepo userRepo;
    @Autowired
    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public void registerUser(Users user) {
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("CUSTOMER");
        }

        if (userRepo.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        if (userRepo.existsByPhone(user.getPhone())) {
            throw new RuntimeException("Phone number already exists");
        }
        userRepo.save(user);
    }

    public Users loginUserByEmail(String email, String password) {
        Users user = userRepo.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("No user found with the email " + email);
        }
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Invalid password");
        }
        return user;
    }

    public Users loginUserByPhone(String phone, String password) {
        Users user = userRepo.findByPhone(phone);
        if (user == null) {
            throw new RuntimeException("No user found with the phone number " + phone);
        }
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Invalid password");
        }
        return user;
    }

}
