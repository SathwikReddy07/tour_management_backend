package com.ysr.controller;

import com.razorpay.RazorpayException;
import com.ysr.model.Bookings;
import com.ysr.model.Users;
import com.ysr.repository.UserRepo;
import com.ysr.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin("*")
public class BookingController {

    private final BookingService bookingService;
    private final UserRepo userRepo;

    public BookingController(BookingService bookingService, UserRepo userRepo) {
        this.bookingService = bookingService;
        this.userRepo = userRepo;
    }

    private Users getLoggedUser(Authentication authentication) {
        Users user = userRepo.findByEmail(authentication.getName());
        if (user == null) {
            throw new RuntimeException("User not found with email: " + authentication.getName());
        }
        return user;
    }

    @PostMapping("/user/bookings")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> createBooking(
            @RequestParam Integer tourId,
            @RequestParam Integer noOfTickets,
            Authentication authentication
    ) {
        Users loggedUser = getLoggedUser(authentication);
        Bookings booking = bookingService.createBooking(loggedUser.getId(), tourId, noOfTickets);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "Booking created successfully",
                        "bookingId",    booking.getBookingId(),
                        "tourId", booking.getTour().getId(),
                        "noOfTickets", booking.getNoOfTickets(),
                        "price", booking.getPrice(),
                        "paymentStatus", booking.getPaymentStatus(),
                        "expiresAt", booking.getExpiresAt()
                ));
    }

    @GetMapping("/user/bookings/{bookingId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> getBookingDetails(
            @PathVariable Long bookingId,
            Authentication authentication
    ) {
        Users loggedUser = getLoggedUser(authentication);
        Bookings booking = bookingService.getBookingDetails(bookingId);
        if (!booking.getCustomer().getId().equals(loggedUser.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    Map.of(
                            "message", "You are not authorized to view this booking"
                    ));
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of(
                        "message", "Booking details",
                        "booking", booking
                ));
    }

//    @PostMapping("/user/bookings/{bookingId}/fail-payment")
//    @PreAuthorize("hasRole('CUSTOMER')")
//    public ResponseEntity<?> failPayment(
//            @PathVariable Long bookingId,
//            Authentication authentication
//    ) {
//        Users loggedUser = getLoggedUser(authentication);
//        Bookings booking = bookingService.failPayment(bookingId, loggedUser.getId());
//        return ResponseEntity.status(HttpStatus.OK)
//                .body(Map.of(
//                        "message", "Payment failed and booking cancelled successfully",
//                        "bookingId", booking.getBookingId(),
//                        "paymentStatus", booking.getPaymentStatus(),
//                        "ticketIssued", booking.getTicketIssued()
//                ));
//    }

    @PostMapping("/user/bookings/{bookingId}/cancel-booking")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> cancelBooking(
            @PathVariable Long bookingId,
            Authentication authentication
    ) {
        Users loggedUser = getLoggedUser(authentication);
        Bookings booking = bookingService.cancelBooking(bookingId, loggedUser.getId());
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of(
                        "message", "Booking cancelled successfully",
                        "bookingId", booking.getBookingId(),
                        "paymentStatus", booking.getPaymentStatus(),
                        "ticketIssued", booking.getTicketIssued()
                ));
    }

    @PostMapping("/user/payment-order/{bookingId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> createPaymentOrder(@PathVariable Long bookingId, Authentication authentication) throws RazorpayException {
        Users loggedUser = getLoggedUser(authentication);
        Map<String, Object> orderDetails = bookingService.createRazorpayOrder(bookingId, loggedUser.getId());
        return ResponseEntity.status(HttpStatus.OK).body(orderDetails);
    }

    @PostMapping("/user/verify-payment")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> verifyPayment(
            @RequestParam Long bookingId,
            @RequestParam String razorpayOrderId,
            @RequestParam String razorpayPaymentId,
            Authentication authentication) throws RazorpayException {
        Users loggedUser = getLoggedUser(authentication);
        Bookings booking = bookingService.confirmBooking(bookingId, loggedUser.getId(), razorpayOrderId, razorpayPaymentId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of(
                        "message", "Payment verified successfully",
                        "bookingId", booking.getBookingId(),
                        "paymentStatus", booking.getPaymentStatus(),
                        "ticketIssued", booking.getTicketIssued(),
                        "transactionId", booking.getTransactionId()));
    }

    @GetMapping("/admin/tickets-summary")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getTicketsSoldPerTour() {
        List<Map<String, Object>> ticketsSoldPerTour = bookingService.getTicketsSoldPerTour();
        if (ticketsSoldPerTour == null || ticketsSoldPerTour.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "No tickets sold for any tour"));
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of(
                        "message", "Tickets sold per tour",
                        "details", ticketsSoldPerTour
                ));
    }

    @GetMapping("/admin/tour-details/{tourId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getTourDetailsWithBookings(@PathVariable Integer tourId) {
        Map<String, Object> tourDetailsWithBookings = bookingService.getTourDetailsWithBookings(tourId);
        if (tourDetailsWithBookings == null || tourDetailsWithBookings.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Tour not found with id: " + tourId));
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of(
                        "message", "Tour details with bookings",
                        "details", tourDetailsWithBookings
                ));
    }

}
