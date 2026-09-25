package com.ysr.repository;

import com.ysr.model.Tours;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TourRepo extends JpaRepository<Tours, Integer> {
}
