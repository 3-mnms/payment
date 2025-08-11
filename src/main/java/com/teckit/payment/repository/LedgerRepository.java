package com.teckit.payment.repository;

import com.teckit.payment.entity.Ledger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LedgerRepository extends JpaRepository<Ledger, Long> {
    boolean existsByPaymentIdAndTransactionTypeAndUserId(
            String payment_id, String transaction_type, String user_id);}
