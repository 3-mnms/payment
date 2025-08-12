package com.teckit.payment.repository;

import com.teckit.payment.entity.PaymentOrder;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentOrderRepository extends JpaRepository<PaymentOrder, String> {
    Optional<PaymentOrder> findByPaymentId(String Id);
    boolean existsByPaymentId(String Id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM PaymentOrder p WHERE p.paymentId = :paymentId")
    Optional<PaymentOrder> findByPaymentIdForUpdate(@Param("paymentId") String paymentId);

    List<PaymentOrder> findByFestivalIdAndBuyerIdAndLedgerUpdatedTrueAndWalletUpdatedTrue(String festivalId, Long buyerId);

}
