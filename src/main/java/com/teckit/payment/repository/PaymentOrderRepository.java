package com.teckit.payment.repository;

import com.teckit.payment.entity.PaymentOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentOrderRepository extends JpaRepository<PaymentOrder, String> {
    Optional<PaymentOrder> findByPaymentId(String Id);
    boolean existsByPaymentId(String Id);

}
