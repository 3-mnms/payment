package com.teckit.payment.repository;

import com.teckit.payment.entity.PaymentEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentEventRepository extends JpaRepository<PaymentEvent,Long> {
    Optional<PaymentEvent> findByPaymentId(String paymentId);



}
