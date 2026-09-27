package com.ysr.repository;

import com.ysr.model.Bookings;
import com.ysr.model.Tours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookingRepo extends JpaRepository<Bookings, Long> {

    @Query("SELECT SUM(b.noOfTickets) FROM Bookings b WHERE b.tour.id = :tourId AND b.paymentStatus = :paymentStatus")
    Integer countOfTicketsSoldByTourIdAndPaymentStatus(
            @Param("tourId") Integer tourId,
            @Param("paymentStatus") Bookings.PaymentStatus paymentStatus
    );

    List<Bookings> findByTourIdAndPaymentStatus(Integer tourId, Bookings.PaymentStatus paymentStatus);

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
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice
    );

}
