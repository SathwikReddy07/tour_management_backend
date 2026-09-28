package com.ysr.config;

import com.ysr.model.Users;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserPrincipal implements UserDetails {

    private Users user;
    private String loginType;
    public UserPrincipal(Users user, String loginType) {
        this.user = user;
        this.loginType = loginType;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(user.getRole() == null || user.getRole().isEmpty()) {
            throw new RuntimeException("User role is not defined");
        }
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user.getRole());
        return List.of(authority);
    }

    @Override
    public @Nullable String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        if(loginType.equalsIgnoreCase("EMAIL")) {
            return user.getEmail();
        }
        else if (loginType.equalsIgnoreCase("PHONE")) {
            return user.getPhone();
        }
        throw new RuntimeException("Invalid login type");
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
