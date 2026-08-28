
package com.ompindia.service;

import com.ompindia.entity.Booking;
import com.ompindia.entity.Payment;
import com.ompindia.repository.BookingRepository;
import com.ompindia.repository.PaymentRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RazorpayService {

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    public RazorpayService(
            BookingRepository bookingRepository,
            PaymentRepository paymentRepository) {

        this.bookingRepository = bookingRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public Payment createRazorpayOrder(Long bookingId)
            throws Exception {

        Booking booking = bookingRepository
                .findById(bookingId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Booking not found."
                        ));

        if (!"PENDING".equalsIgnoreCase(
                booking.getStatus())) {

            throw new IllegalStateException(
                    "Booking is not available for payment."
            );
        }

        Payment existingPayment =
                paymentRepository
                        .findByBookingId(bookingId)
                        .orElse(null);

        if (existingPayment != null &&
                existingPayment.getGatewayOrderId() != null) {

            return existingPayment;
        }

        RazorpayClient razorpayClient =
                new RazorpayClient(
                        keyId,
                        keySecret
                );

        int amountInPaise =
                booking.getPrice() * 100;

        JSONObject orderRequest =
                new JSONObject();

        orderRequest.put(
                "amount",
                amountInPaise
        );

        orderRequest.put(
                "currency",
                "INR"
        );

        orderRequest.put(
                "receipt",
                "OMP-" + booking.getId()
        );

        JSONObject notes =
                new JSONObject();

        notes.put(
                "booking_id",
                booking.getId()
        );

        notes.put(
                "pixel_number",
                booking.getPixelNumber()
        );

        orderRequest.put(
                "notes",
                notes
        );

        Order order =
                razorpayClient.orders.create(
                        orderRequest
                );

        String orderId =
                order.get("id");

        Payment payment;

        if (existingPayment == null) {

            payment = new Payment();

            payment.setBookingId(
                    booking.getId()
            );

            payment.setAmount(
                    booking.getPrice()
            );

            payment.setCurrency(
                    "INR"
            );

        } else {

            payment = existingPayment;

        }

        payment.setPaymentStatus(
                "PENDING"
        );

        payment.setGatewayOrderId(
                orderId
        );

        return paymentRepository.save(
                payment
        );
    }

    public String getKeyId() {
        return keyId;
    }
}
