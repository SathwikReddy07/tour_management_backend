package com.ysr.repository;

import com.ysr.model.Transport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransportRepo extends JpaRepository<Transport, Integer> {
}
