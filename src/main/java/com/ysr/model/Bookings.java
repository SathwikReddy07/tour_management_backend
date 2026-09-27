package com.ysr.model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "bookings")
public class Bookings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private Users customer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tour_id", referencedColumnName = "id", nullable = false)
    private Tours tour;

    private Integer noOfTickets;
    private Double price;

    public enum PaymentStatus {
        COMPLETED,
        PENDING,
        FAILED
    }

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private Date bookingDate;
    private Boolean ticketIssued;
    private String transactionId;

    public Boolean checkAvailability() {
        return tour.getTicketsAvailable() >= noOfTickets;
    }

    public void confirmBooking() {
        if (paymentStatus == PaymentStatus.COMPLETED && checkAvailability()) {
            tour.setTicketsAvailable(tour.getTicketsAvailable() - noOfTickets);
            ticketIssued = true;
        } else {
            ticketIssued = false;
        }
    }

    public void handlePaymentFailure() {
        paymentStatus = PaymentStatus.FAILED;
        ticketIssued = false;
    }

    public Bookings() {}

    public Bookings(Users customer, Tours tour, Integer noOfTickets, Double price, PaymentStatus paymentStatus,
                    Date bookingDate, Boolean ticketIssued, String transactionId) {
        this.customer = customer;
        this.tour = tour;
        this.noOfTickets = noOfTickets;
        this.price = price;
        this.paymentStatus = paymentStatus;
        this.bookingDate = bookingDate;
        this.ticketIssued = ticketIssued;
        this.transactionId = transactionId;
    }

    public Long getBookingId() {
        return bookingId;
    }
    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public Users getCustomer() {
        return customer;
    }
    public void setCustomer(Users customer) {
        this.customer = customer;
    }

    public Tours getTour() {
        return tour;
    }
    public void setTour(Tours tour) {
        this.tour = tour;
    }

    public Integer getNoOfTickets() {
        return noOfTickets;
    }
    public void setNoOfTickets(Integer noOfTickets) {
        this.noOfTickets = noOfTickets;
    }

    public Double getPrice() {
        return price;
    }
    public void setPrice(Double price) {
        this.price = price;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }
    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Date getBookingDate() {
        return bookingDate;
    }
    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }

    public Boolean getTicketIssued() {
        return ticketIssued;
    }
    public void setTicketIssued(Boolean ticketIssued) {
        this.ticketIssued = ticketIssued;
    }

    public String getTransactionId() {
        return transactionId;
    }
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    @Override
    public String toString() {
        return "Bookings{" +
                "bookingId=" + bookingId +
                ", customer=" + customer +
                ", tour=" + tour +
                ", noOfTickets=" + noOfTickets +
                ", price=" + price +
                ", paymentStatus=" + paymentStatus +
                ", bookingDate=" + bookingDate +
                ", ticketIssued=" + ticketIssued +
                ", transactionId='" + transactionId + '\'' +
                '}';
    }

}
