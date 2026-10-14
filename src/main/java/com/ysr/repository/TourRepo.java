package com.ysr.repository;

import com.ysr.model.Tours;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface TourRepo extends JpaRepository<Tours, Integer> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Tours t WHERE t.id = :id")
    Optional<Tours> findByIdForUpdate(@Param("id") Integer id);

    @Query("SELECT t FROM Tours t WHERE " +
            "(:country IS NULL OR t.location.country = :country) AND " +
            "(:lodgingType IS NULL OR t.lodging.type = :lodgingType) AND " +
            "(:transportationType IS NULL OR t.transport.type = :transportationType) AND " +
            "(:minPrice IS NULL OR t.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR t.price <= :maxPrice)")
    List<Tours> filterTours(
            @Param("country") String country,
            @Param("lodgingType") String lodgingType,
            @Param("transportationType") String transportationType,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice
    );

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
