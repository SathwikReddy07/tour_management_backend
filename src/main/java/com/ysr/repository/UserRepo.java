package com.ysr.repository;

import com.ysr.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepo extends JpaRepository<Users, Integer> {

    Boolean existsByEmail(String email);
    Boolean existsByPhone(String phone);

    Users findByEmail(String email);
    Users findByPhone(String phone);

    @Query("SELECT u.role FROM Users u WHERE u.email = :email")
    String findRoleByUsername(@Param("email") String email);
}
