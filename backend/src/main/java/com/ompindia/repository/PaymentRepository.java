
package com.ompindia.repository;

import com.ompindia.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository
        extends JpaRepository<Payment, Long> {

    Optional<Payment> findByBookingId(Long bookingId);

    Optional<Payment> findByGatewayOrderId(
            String gatewayOrderId
    );

    Optional<Payment> findByGatewayPaymentId(
            String gatewayPaymentId
    );

    List<Payment> findByPaymentStatus(
            String paymentStatus
    );
}
