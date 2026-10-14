package com.ysr.repository;

import com.ysr.model.Bookings;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepo extends JpaRepository<Bookings, Long> {

    @Query("SELECT SUM(b.noOfTickets) FROM Bookings b WHERE b.tour.id = :tourId AND b.paymentStatus = :paymentStatus")
    Integer countOfTicketsSoldByTourIdAndPaymentStatus(
            @Param("tourId") Integer tourId,
            @Param("paymentStatus") Bookings.PaymentStatus paymentStatus
    );

    List<Bookings> findByTourIdAndPaymentStatus(Integer tourId, Bookings.PaymentStatus paymentStatus);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Bookings b WHERE b.bookingId = :id")
    Optional<Bookings> findByIdForUpdate(@Param("id") Long id);

    @Query("SELECT SUM(b.price) FROM Bookings b WHERE b.tour.id = :tourId AND b.paymentStatus = :paymentStatus")
    BigDecimal getTotalRevenueByTourIdAndPaymentStatus(
            @Param("tourId") Integer tourId,
            @Param("paymentStatus") Bookings.PaymentStatus paymentStatus
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Bookings b WHERE b.paymentStatus = :status AND b.expiresAt < :time")
    List<Bookings> findExpiredBookingsForUpdate(
            @Param("status") Bookings.PaymentStatus status,
            @Param("time") LocalDateTime time
    );

    List<Bookings> findByTourId(Integer tourId);

}
