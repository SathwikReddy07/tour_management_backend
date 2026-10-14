package com.ysr.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    @Column(nullable = false)
    private Integer noOfTickets;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    public enum PaymentStatus {
        COMPLETED,
        PENDING,
        FAILED,
        CANCELLED,
        EXPIRED
    }

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Column(nullable = false)
    private LocalDateTime bookingDateTime;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private Boolean ticketIssued;

    @Column(unique = true)
    private String transactionId;

    @Column(unique = true)
    private String razorpayOrderId;

    public Bookings() {}

    public Bookings(Users customer, Tours tour, Integer noOfTickets, BigDecimal price, PaymentStatus paymentStatus,
                    LocalDateTime bookingDateTime, LocalDateTime expiresAt, Boolean ticketIssued, String transactionId, String razorpayOrderId) {
        this.customer = customer;
        this.tour = tour;
        this.noOfTickets = noOfTickets;
        this.price = price;
        this.paymentStatus = paymentStatus;
        this.bookingDateTime = bookingDateTime;
        this.expiresAt = expiresAt;
        this.ticketIssued = ticketIssued;
        this.transactionId = transactionId;
        this.razorpayOrderId = razorpayOrderId;
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

    public BigDecimal getPrice() {
        return price;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }
    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public LocalDateTime getBookingDateTime() {
        return bookingDateTime;
    }
    public void setBookingDateTime(LocalDateTime bookingDateTime) {
        this.bookingDateTime = bookingDateTime;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
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

    public String getRazorpayOrderId() {
        return razorpayOrderId;
    }
    public void setRazorpayOrderId(String razorpayOrderId) {
        this.razorpayOrderId = razorpayOrderId;
    }

}
