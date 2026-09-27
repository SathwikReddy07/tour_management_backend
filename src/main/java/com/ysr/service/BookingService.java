package com.ysr.service;

import com.ysr.exception.BookingNotFoundException;
import com.ysr.exception.InsufficientTicketsException;
import com.ysr.exception.TourNotFoundException;
import com.ysr.model.Bookings;
import com.ysr.model.Tours;
import com.ysr.model.Users;
import com.ysr.repository.BookingRepo;
import com.ysr.repository.TourRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BookingService {

    private BookingRepo bookingRepo;
    @Autowired
    public void setBookingRepo(BookingRepo bookingRepo) {
        this.bookingRepo = bookingRepo;
    }

    private TourRepo tourRepo;
    @Autowired
    public void setTourRepo(TourRepo tourRepo) {
        this.tourRepo = tourRepo;
    }

    public Bookings createBooking(Users customer, Integer tourId, Integer noOfTickets) {
        Tours tour = tourRepo.findById(tourId)
                .orElseThrow(() -> new TourNotFoundException("Tour not found with id: " + tourId));
        if (noOfTickets > tour.getTicketsAvailable()) {
            throw new InsufficientTicketsException("Not enough tickets available for tour with id: " + tourId);
        }
        Bookings booking = new Bookings();
        booking.setCustomer(customer);
        booking.setTour(tour);
        booking.setNoOfTickets(noOfTickets);
        booking.setPrice(tour.getPrice() * noOfTickets);
        booking.setPaymentStatus(Bookings.PaymentStatus.PENDING);
        booking.setBookingDate(new Date());
        booking.setTicketIssued(false);
        booking.setTransactionId(null);
        bookingRepo.save(booking);
        return booking;
    }

    public Bookings confirmBooking(Long id, String transactionId) {
        Bookings booking = bookingRepo.findById(id)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + id));
        if (booking.getPaymentStatus().equals(Bookings.PaymentStatus.PENDING)) {
            booking.setPaymentStatus(Bookings.PaymentStatus.COMPLETED);
            booking.confirmBooking();
            booking.setTransactionId(transactionId);
            booking.getTour().setTicketsAvailable
                    (booking.getTour().getTicketsAvailable() - booking.getNoOfTickets());
        }
        return bookingRepo.save(booking);
    }

    public List<Map<String, Object>> getTicketsSoldPerTour() {
        List<Tours> tours = tourRepo.findAll();
        List<Map<String, Object>> result = tours.stream().map(tour -> {
            Integer ticketsSold = bookingRepo.countOfTicketsSoldByTourIdAndPaymentStatus(tour.getId(), Bookings.PaymentStatus.COMPLETED) != null
                    ? bookingRepo.countOfTicketsSoldByTourIdAndPaymentStatus(tour.getId(), Bookings.PaymentStatus.COMPLETED) : 0;
            Map <String, Object> map = new HashMap<>();
            map.put("tourId", tour.getId());
            map.put("tourName", tour.getName());
            map.put("ticketsSold", ticketsSold);
            map.put("ticketsAvailable", tour.getTicketsAvailable());
            map.put("totalRevenue", ticketsSold * tour.getPrice());
            return map;
        }).toList();
        return result;
    }

    public Map<String, Object> getTourDetailsWithBookings(Integer tourId) {
        Tours tour = tourRepo.findById(tourId)
                .orElseThrow(() -> new TourNotFoundException("Tour not found with id: " + tourId));
        List<Bookings> bookings = bookingRepo.findByTourIdAndPaymentStatus(tourId, Bookings.PaymentStatus.COMPLETED);
        Integer ticketsSold = bookingRepo.countOfTicketsSoldByTourIdAndPaymentStatus(tourId, Bookings.PaymentStatus.COMPLETED) != null
                ? bookingRepo.countOfTicketsSoldByTourIdAndPaymentStatus(tourId, Bookings.PaymentStatus.COMPLETED) : 0;
        List<Map<String, Object>> bookingDetails = bookings.stream().map(booking -> {
           Map <String, Object> bookingInfo = new HashMap<>();
           bookingInfo.put("bookingId", booking.getBookingId());
           bookingInfo.put("customerName", booking.getCustomer().getName());
           bookingInfo.put("customerEmail", booking.getCustomer().getEmail());
           bookingInfo.put("customerPhone", booking.getCustomer().getPhone());
           bookingInfo.put("numberOfTickets", booking.getNoOfTickets());
           bookingInfo.put("price", booking.getPrice());
           bookingInfo.put("bookingDate", booking.getBookingDate());
           bookingInfo.put("paymentStatus", booking.getPaymentStatus());
           return bookingInfo;
        }).toList();
        Map<String, Object> tourDetails = new HashMap<>();
        tourDetails.put("tourId", tour.getId());
        tourDetails.put("tourName", tour.getName());
        tourDetails.put("ticketsSold", ticketsSold);
        tourDetails.put("ticketsAvailable", tour.getTicketsAvailable());
        tourDetails.put("totalRevenue", ticketsSold * tour.getPrice());
        tourDetails.put("bookingDetails", bookingDetails);
        return tourDetails;
    }

    public List<Tours> getToursByFilter(String country, String lodgingType, String transportationType, Double minPrice, Double maxPrice) {
        return bookingRepo.filterTours(country, lodgingType, transportationType, minPrice, maxPrice);
    }

}
