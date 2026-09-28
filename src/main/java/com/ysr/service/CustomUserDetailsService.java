package com.ysr.service;

import com.ysr.config.UserPrincipal;
import com.ysr.model.Users;
import com.ysr.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Primary
public class CustomUserDetailsService implements UserDetailsService {

    private UserRepo userRepo;
    @Autowired
    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    private String loginType;
    public CustomUserDetailsService() {
        this.loginType = "EMAIL";
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users u1 = userRepo.findByEmail(username);
        Users u2 = userRepo.findByPhone(username);
        if (u1 == null && u2 == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        } else if (u1 != null) {
            return new UserPrincipal(u1, "EMAIL");
        } else {
            return new UserPrincipal(u2, "PHONE");
        }
    }
}
