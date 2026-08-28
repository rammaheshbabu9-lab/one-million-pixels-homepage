package com.ompindia.controller;

import com.ompindia.entity.Payment;
import com.ompindia.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "${app.frontend.url}")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(
            PaymentService paymentService) {

        this.paymentService = paymentService;
    }

    @PostMapping("/create/{bookingId}")
    public ResponseEntity<Payment> createPayment(
            @PathVariable Long bookingId) {

        Payment payment =
                paymentService.createPayment(bookingId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(payment);
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<Payment> getPayment(
            @PathVariable Long paymentId) {

        return ResponseEntity.ok(
                paymentService.getPayment(paymentId)
        );
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<Payment>
    getPaymentByBooking(
            @PathVariable Long bookingId) {

        Payment payment =
                paymentService.getPaymentByBooking(
                        bookingId
                );

        if (payment == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(payment);
    }

    /*
     * Temporary success endpoint for development.
     *
     * IMPORTANT:
     * Do NOT use this endpoint as-is in production.
     * Real payment gateway webhook/signature
     * verification must be added before deployment.
     */
    @PostMapping("/success/{paymentId}")
    public ResponseEntity<Payment> markSuccess(
            @PathVariable Long paymentId,
            @RequestParam String gatewayPaymentId,
            @RequestParam String gatewaySignature) {

        Payment payment =
                paymentService.markPaymentSuccess(
                        paymentId,
                        gatewayPaymentId,
                        gatewaySignature
                );

        return ResponseEntity.ok(payment);
    }
}
