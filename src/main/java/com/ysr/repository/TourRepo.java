package com.ysr.repository;

import com.ysr.model.Tours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TourRepo extends JpaRepository<Tours, Integer> {

    @Query("SELECT t FROM Tours t" +
            " JOIN FETCH t.location" +
            " JOIN FETCH t.lodging" +
            " JOIN FETCH t.transport")
    List<Tours> getAllToursWithDetails();

    @Query("SELECT t FROM Tours t" +
            " JOIN FETCH t.location" +
            " JOIN FETCH t.lodging" +
            " JOIN FETCH t.transport" +
            " WHERE t.id = :id")
    Tours getTourByIdWithDetails(@Param("id") Integer id);

}
