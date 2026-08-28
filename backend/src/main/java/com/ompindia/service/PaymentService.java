
package com.ompindia.service;

import com.ompindia.entity.Booking;
import com.ompindia.entity.Payment;
import com.ompindia.repository.BookingRepository;
import com.ompindia.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;

    public PaymentService(
            PaymentRepository paymentRepository,
            BookingRepository bookingRepository) {

        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    public Payment createPayment(Long bookingId) {

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

        Payment payment = new Payment();

        payment.setBookingId(booking.getId());

        // Always take amount from server-side booking.
        payment.setAmount(booking.getPrice());

        payment.setCurrency("INR");

        payment.setPaymentStatus("PENDING");

        return paymentRepository.save(payment);
    }

    public Payment getPayment(Long paymentId) {

        return paymentRepository
                .findById(paymentId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Payment not found."
                        ));
    }

    public Payment getPaymentByBooking(
            Long bookingId) {

        return paymentRepository
                .findByBookingId(bookingId)
                .orElse(null);
    }

    @Transactional
    public Payment markPaymentSuccess(
            Long paymentId,
            String gatewayPaymentId,
            String gatewaySignature) {

        Payment payment = getPayment(paymentId);

        payment.setPaymentStatus("SUCCESS");

        payment.setGatewayPaymentId(
                gatewayPaymentId
        );

        payment.setGatewaySignature(
                gatewaySignature
        );

        Booking booking = bookingRepository
                .findById(payment.getBookingId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Booking not found."
                        ));

        booking.setStatus("CONFIRMED");

        bookingRepository.save(booking);

        return paymentRepository.save(payment);
    }
}
