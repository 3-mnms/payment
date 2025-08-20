package com.teckit.payment.repository;

import com.teckit.payment.entity.PaymentCancellation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentCancellationRepository extends JpaRepository<PaymentCancellation, Integer> {
}
