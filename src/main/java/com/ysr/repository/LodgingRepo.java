package com.ysr.repository;

import com.ysr.model.Lodging;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LodgingRepo extends JpaRepository<Lodging, Integer> {
}
