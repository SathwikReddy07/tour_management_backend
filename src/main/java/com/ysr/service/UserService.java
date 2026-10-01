package com.ysr.service;

import com.ysr.exception.EmailAlreadyExistsException;
import com.ysr.exception.InvalidCredentialsException;
import com.ysr.exception.UserNotFoundException;
import com.ysr.exception.PhoneNumberAlreadyExistsException;
import com.ysr.model.Users;
import com.ysr.repository.UserRepo;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private PasswordEncoder passwordEncoder;
    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    private UserRepo userRepo;
    @Autowired
    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public void registerUser(Users user) {
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("ROLE_CUSTOMER");
        }

        if (userRepo.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        if (userRepo.existsByPhone(user.getPhone())) {
            throw new PhoneNumberAlreadyExistsException("Phone number already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepo.save(user);
    }

    public void deleteUserById(Integer id, Authentication authentication) {
        Users user = userRepo.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        Users authenticatedUser = userRepo.findByEmail(authentication.getName());
        if (authenticatedUser == null || !authenticatedUser.getId().equals(user.getId())) {
            throw new InvalidCredentialsException("You are not authorized to delete this user");
        }
        userRepo.delete(user);
    }

    public Users loginUserByEmail(String email, String password) {
        Users user = userRepo.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("No user found with the email " + email);
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException("Invalid password");
        }
        return user;
    }

    public Users loginUserByPhone(String phone, String password) {
        Users user = userRepo.findByPhone(phone);
        if (user == null) {
            throw new UserNotFoundException("No user found with the phone number " + phone);
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException("Invalid password");
        }
        return user;
    }

    public void deleteUserByEmail(String email) {
        Users user = userRepo.findByEmail(email);
        userRepo.delete(user);
    }

}
