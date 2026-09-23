package com.ysr.repository;

import com.ysr.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepo extends JpaRepository<Location, Integer> {
}
