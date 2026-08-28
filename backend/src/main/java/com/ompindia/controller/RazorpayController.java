
package com.ompindia.controller;

import com.ompindia.entity.Payment;
import com.ompindia.service.RazorpayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/razorpay")
@CrossOrigin(origins = "${app.frontend.url}")
public class RazorpayController {

    private final RazorpayService razorpayService;

    public RazorpayController(
            RazorpayService razorpayService) {
        this.razorpayService = razorpayService;
    }

    @PostMapping("/order/{bookingId}")
    public ResponseEntity<?> createOrder(
            @PathVariable Long bookingId) {

        try {

            Payment payment =
                    razorpayService
                            .createRazorpayOrder(
                                    bookingId
                            );

            Map<String, Object> response =
                    new HashMap<>();

            response.put(
                    "paymentId",
                    payment.getId()
            );

            response.put(
                    "bookingId",
                    payment.getBookingId()
            );

            response.put(
                    "amount",
                    payment.getAmount()
            );

            response.put(
                    "currency",
                    payment.getCurrency()
            );

            response.put(
                    "orderId",
                    payment.getGatewayOrderId()
            );

            response.put(
                    "keyId",
                    razorpayService.getKeyId()
            );

            response.put(
                    "status",
                    payment.getPaymentStatus()
            );

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "error",
                                    e.getMessage()
                            )
                    );

        } catch (IllegalStateException e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "error",
                                    e.getMessage()
                            )
                    );

        } catch (Exception e) {

            return ResponseEntity
                    .internalServerError()
                    .body(
                            Map.of(
                                    "error",
                                    "Unable to create payment order."
                            )
                    );
        }
    }
}
