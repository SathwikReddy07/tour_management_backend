package com.ysr.service;

import com.razorpay.Payment;
import com.razorpay.RazorpayException;
import com.ysr.exception.BookingNotFoundException;
import com.ysr.exception.InsufficientTicketsException;
import com.ysr.exception.TourNotFoundException;
import com.ysr.exception.UserNotFoundException;
import com.ysr.model.Bookings;
import com.ysr.model.Tours;
import com.ysr.model.Users;
import com.ysr.repository.BookingRepo;
import com.ysr.repository.TourRepo;
import com.ysr.repository.UserRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BookingService {

    private final BookingRepo bookingRepo;
    private final UserRepo userRepo;
    private final TourRepo tourRepo;
    private final RazorpayService razorpayService;

    public BookingService(
            BookingRepo bookingRepo,
            UserRepo userRepo,
            TourRepo tourRepo,
            RazorpayService razorpayService
    ) {
        this.bookingRepo = bookingRepo;
        this.userRepo = userRepo;
        this.tourRepo = tourRepo;
        this.razorpayService = razorpayService;
    }

    @Transactional
    public Bookings createBooking(Integer customerId, Integer tourId, Integer noOfTickets) {
        if (noOfTickets == null || noOfTickets <= 0) {
            throw new IllegalArgumentException("Number of tickets must be greater than zero.");
        }
        Tours tour = tourRepo.findByIdForUpdate(tourId)
                .orElseThrow(() -> new TourNotFoundException("Tour not found with id: " + tourId));
        Users customer = userRepo.findById(customerId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + customerId));
        if (noOfTickets > tour.getTicketsAvailable()) {
            throw new InsufficientTicketsException("Not enough tickets available for tour with id: " + tourId);
        }
        tour.setTicketsAvailable(tour.getTicketsAvailable() - noOfTickets);
        LocalDateTime now = LocalDateTime.now();
        BigDecimal totalPrice = tour.getPrice().multiply(BigDecimal.valueOf(noOfTickets));

        Bookings booking = new Bookings();
        booking.setCustomer(customer);
        booking.setTour(tour);
        booking.setNoOfTickets(noOfTickets);
        booking.setPrice(totalPrice);
        booking.setPaymentStatus(Bookings.PaymentStatus.PENDING);
        booking.setBookingDateTime(now);
        booking.setExpiresAt(now.plusMinutes(15));
        booking.setTicketIssued(false);
        booking.setTransactionId(null);
        booking.setRazorpayOrderId(null);
        bookingRepo.save(booking);
        return booking;
    }

    @Transactional
    public Map<String, Object> createRazorpayOrder(Long bookingId, Integer customerId) throws RazorpayException {
        Bookings booking = bookingRepo.findByIdForUpdate(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + bookingId));
        if (!booking.getCustomer().getId().equals(customerId)) {
            throw new IllegalArgumentException("Booking does not belong to the specified customer.");
        }
        if (booking.getPaymentStatus() != Bookings.PaymentStatus.PENDING) {
            throw new IllegalStateException("Booking is not in a pending state and cannot create Razorpay order.");
        }
        if (LocalDateTime.now().isAfter(booking.getExpiresAt())) {
            throw new IllegalStateException("Booking has expired and cannot create Razorpay order.");
        }
        if (booking.getRazorpayOrderId() != null && !booking.getRazorpayOrderId().isBlank()) {
            long amount = booking.getPrice().multiply(BigDecimal.valueOf(100)).longValueExact();
            return Map.of(
                    "orderId", booking.getRazorpayOrderId(),
                    "amount", amount,
                    "currency", "INR",
                    "receipt", "booking_" + booking.getBookingId()
            );
        }
        Map<String, Object> result = razorpayService.createOrder(booking);
        String razorpayOrderId = (String) result.get("orderId");
        booking.setRazorpayOrderId(razorpayOrderId);
        bookingRepo.save(booking);
        return result;
    }

    @Transactional
    public Bookings confirmBooking(Long id, Integer customerId, String razorpayOrderId, String razorpayPaymentId) throws RazorpayException {
        if (razorpayOrderId == null || razorpayOrderId.isBlank()) {
            throw new IllegalArgumentException("Razorpay order ID cannot be null or blank.");
        }
        if (razorpayPaymentId == null || razorpayPaymentId.isBlank()) {
            throw new IllegalArgumentException("Razorpay payment ID cannot be null or blank.");
        }
        Bookings booking = bookingRepo.findByIdForUpdate(id)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + id));
        if (!booking.getCustomer().getId().equals(customerId)) {
            throw new IllegalArgumentException("Booking does not belong to the specified customer.");
        }
        if (booking.getPaymentStatus() != Bookings.PaymentStatus.PENDING) {
            throw new IllegalStateException("Booking is not in a pending state and cannot be confirmed.");
        }
        if (LocalDateTime.now().isAfter(booking.getExpiresAt())) {
            throw new IllegalStateException("Booking has expired and cannot be confirmed.");
        }
        if (!razorpayOrderId.equals(booking.getRazorpayOrderId())) {
            throw new IllegalArgumentException("Razorpay order ID does not match the booking's order ID.");
        }
        Payment payment = razorpayService.fetchPaymentDetails(razorpayPaymentId);
        String paymentOrderId = payment.get("order_id");
        if (!razorpayOrderId.equals(paymentOrderId)) {
            throw new IllegalArgumentException("Razorpay payment order ID does not match the booking's order ID.");
        }
        String paymentStatus = payment.get("status");
        if (!"captured".equalsIgnoreCase(paymentStatus)) {
            throw new IllegalStateException("Payment is not captured and cannot be confirmed.");
        }

        long razorpayAmount = ((Number) payment.get("amount")).longValue();
        long bookingAmount = booking.getPrice().multiply(BigDecimal.valueOf(100)).longValueExact();
        if (razorpayAmount != bookingAmount) {
            throw new IllegalArgumentException("Razorpay payment amount does not match the booking amount.");
        }
        booking.setPaymentStatus(Bookings.PaymentStatus.COMPLETED);
        booking.setTicketIssued(true);
        booking.setTransactionId(razorpayPaymentId);
        return bookingRepo.save(booking);
    }

    @Transactional
    public Bookings failPayment(Long id, Integer customerId) {
        Bookings booking = bookingRepo.findByIdForUpdate(id)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + id));
        if (!booking.getCustomer().getId().equals(customerId)) {
            throw new IllegalArgumentException("Booking does not belong to the specified customer.");
        }
        if (booking.getPaymentStatus() != Bookings.PaymentStatus.PENDING) {
            throw new IllegalStateException("Booking is not in a pending state and cannot be marked as failed.");
        }
        booking.setPaymentStatus(Bookings.PaymentStatus.FAILED);
        booking.setTicketIssued(false);
        booking.setTransactionId(null);
        Tours tour = tourRepo.findByIdForUpdate(booking.getTour().getId())
                .orElseThrow(() -> new TourNotFoundException("Tour not found with id: " + booking.getTour().getId()));
        tour.setTicketsAvailable(tour.getTicketsAvailable() + booking.getNoOfTickets());
        return bookingRepo.save(booking);
    }

    @Transactional
    public Bookings cancelBooking(Long id, Integer customerId) {
        Bookings booking = bookingRepo.findByIdForUpdate(id)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + id));
        if (!booking.getCustomer().getId().equals(customerId)) {
            throw new IllegalArgumentException("Booking does not belong to the specified customer.");
        }
        if (booking.getPaymentStatus() != Bookings.PaymentStatus.PENDING) {
            throw new IllegalStateException("Booking is not in a pending state and cannot be cancelled.");
        }
        booking.setPaymentStatus(Bookings.PaymentStatus.CANCELLED);
        booking.setTicketIssued(false);
        booking.setTransactionId(null);
        Tours tour = tourRepo.findByIdForUpdate(booking.getTour().getId())
                .orElseThrow(() -> new TourNotFoundException("Tour not found with id: " + booking.getTour().getId()));
        tour.setTicketsAvailable(tour.getTicketsAvailable() + booking.getNoOfTickets());
        return bookingRepo.save(booking);
    }

    public Bookings getBookingDetails(Long id) {
        return bookingRepo.findById(id)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + id));
    }

    public List<Map<String, Object>> getTicketsSoldPerTour() {
        List<Tours> tours = tourRepo.findAll();
        List<Map<String, Object>> result = tours.stream().map(tour -> {
            Integer ticketsSold = bookingRepo.countOfTicketsSoldByTourIdAndPaymentStatus(tour.getId(), Bookings.PaymentStatus.COMPLETED);
            if (ticketsSold == null) {
                ticketsSold = 0;
            }
            BigDecimal totalRevenue = bookingRepo.getTotalRevenueByTourIdAndPaymentStatus(tour.getId(), Bookings.PaymentStatus.COMPLETED);
            if (totalRevenue == null) {
                totalRevenue = BigDecimal.ZERO;
            }
            Map<String, Object> map = new HashMap<>();
            map.put("tourId", tour.getId());
            map.put("tourName", tour.getName());
            map.put("ticketsSold", ticketsSold);
            map.put("ticketsAvailable", tour.getTicketsAvailable());
            map.put("totalRevenue", totalRevenue);
            return map;
        }).toList();
        return result;
    }

    public Map<String, Object> getTourDetailsWithBookings(Integer tourId) {
        Tours tour = tourRepo.findById(tourId)
                .orElseThrow(() -> new TourNotFoundException("Tour not found with id: " + tourId));
        List<Bookings> bookings = bookingRepo.findByTourIdAndPaymentStatus(tourId, Bookings.PaymentStatus.COMPLETED);
        Integer ticketsSold = bookings.stream().mapToInt(Bookings::getNoOfTickets).sum();
        List<Map<String, Object>> bookingDetails = bookings.stream().map(booking -> {
            Map<String, Object> bookingInfo = new HashMap<>();
            bookingInfo.put("bookingId", booking.getBookingId());
            bookingInfo.put("customerName", booking.getCustomer().getName());
            bookingInfo.put("customerEmail", booking.getCustomer().getEmail());
            bookingInfo.put("customerPhone", booking.getCustomer().getPhone());
            bookingInfo.put("numberOfTickets", booking.getNoOfTickets());
            bookingInfo.put("price", booking.getPrice());
            bookingInfo.put("bookingDateTime", booking.getBookingDateTime());
            bookingInfo.put("paymentStatus", booking.getPaymentStatus());
            return bookingInfo;
        }).toList();
        BigDecimal totalRevenue = bookings.stream()
                .map(Bookings::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        Map<String, Object> tourDetails = new HashMap<>();
        tourDetails.put("tourId", tour.getId());
        tourDetails.put("tourName", tour.getName());
        tourDetails.put("ticketsSold", ticketsSold);
        tourDetails.put("ticketsAvailable", tour.getTicketsAvailable());
        tourDetails.put("totalRevenue", totalRevenue);
        tourDetails.put("bookingDetails", bookingDetails);
        return tourDetails;
    }

}
