package com.ysr.repository;

import com.ysr.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<Users, Integer> {

    Boolean existsByEmail(String email);
    Boolean existsByPhone(String phone);

    Users findByEmail(String email);
    Users findByPhone(String phone);
}
