package com.ysr.service;

import com.ysr.model.Users;
import com.ysr.repository.UserRepo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private UserRepo userRepo;
    private PasswordEncoder passwordEncoder;

    public CustomOAuth2UserService(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User user = super.loadUser(userRequest);
        String email = user.getAttribute("email");
        String username = user.getAttribute("username");
        String phone = user.getAttribute("phone");

        if (userRepo.findByEmail(email) != null) {
            return user;
        }
        else if (userRepo.findByPhone(phone) != null) {
            return user;
        }
        Users newUser = new Users();
        newUser.setEmail(email);
        newUser.setName(username);
        newUser.setPhone(phone);
        newUser.setRole("ROLE_CUSTOMER");
        newUser.setPassword(passwordEncoder.encode(username+"@travelora"));
        userRepo.save(newUser);
        return user;
    }

}