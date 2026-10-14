package com.ysr.service;

import com.razorpay.Order;
import com.razorpay.Payment;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.ysr.model.Bookings;
import com.ysr.repository.BookingRepo;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class RazorpayService {

    private final RazorpayClient client;

    public RazorpayService(
            @Value("${razorpay.key-id}") String keyId,
            @Value("${razorpay.key-secret}") String keySecret
    ) throws RazorpayException {
        this.client = new RazorpayClient(keyId, keySecret);
    }

    public Map<String, Object> createOrder(Bookings booking) throws RazorpayException {
        long amountInPaise = booking.getPrice().multiply(BigDecimal.valueOf(100)).longValueExact();
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amountInPaise);
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "booking_" + booking.getBookingId());
        Order order = client.orders.create(orderRequest);
        String razorpayOrderId = order.get("id");
        return Map.of(
                "orderId", razorpayOrderId,
                "amount", order.get("amount"),
                "currency", order.get("currency"),
                "receipt", order.get("receipt")
        );
    }

    public Payment fetchPaymentDetails(String paymentId) throws RazorpayException {
        return client.payments.fetch(paymentId);
    }

}
