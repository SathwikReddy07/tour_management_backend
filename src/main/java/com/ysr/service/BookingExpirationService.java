package com.ysr.service;

import com.ysr.model.Bookings;
import com.ysr.model.Tours;
import com.ysr.repository.BookingRepo;
import com.ysr.repository.TourRepo;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingExpirationService {

    private final BookingRepo bookingRepo;
    private final TourRepo tourRepo;

    public BookingExpirationService(BookingRepo bookingRepo, TourRepo tourRepo) {
        this.bookingRepo = bookingRepo;
        this.tourRepo = tourRepo;
    }

    @Transactional
    @Scheduled(fixedRate = 60000)
    public void expirePendingBookings() {
        List<Bookings> bookings = bookingRepo.findExpiredBookingsForUpdate(Bookings.PaymentStatus.PENDING, LocalDateTime.now());
        for (Bookings booking : bookings) {
            Tours tour = tourRepo.findByIdForUpdate(booking.getTour().getId())
                    .orElseThrow(() -> new RuntimeException("Tour not found with id: " + booking.getTour().getId()));
            tour.setTicketsAvailable(tour.getTicketsAvailable() + booking.getNoOfTickets());
            booking.setPaymentStatus(Bookings.PaymentStatus.EXPIRED);
            booking.setTicketIssued(false);
        }
    }

}
