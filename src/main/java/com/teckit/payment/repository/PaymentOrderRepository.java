package com.teckit.payment.repository;

import com.teckit.payment.entity.PaymentOrder;
import com.teckit.payment.enumeration.PaymentOrderStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
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


    Page<PaymentOrder> findByBuyerIdAndPaymentOrderStatusInAndLedgerUpdatedTrueAndWalletUpdatedTrue(
            Long buyerId,
            Collection<PaymentOrderStatus> statuses,
            Pageable pageable
    );
    Optional<PaymentOrder> findByBookingId(String bookingId);
}
