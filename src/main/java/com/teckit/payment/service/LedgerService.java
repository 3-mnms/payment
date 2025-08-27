package com.teckit.payment.service;

import com.teckit.payment.dto.request.LedgerDTO;
import com.teckit.payment.entity.Ledger;
import com.teckit.payment.enumeration.LedgerTransactionStatus;
import com.teckit.payment.repository.LedgerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LedgerService {
    private final LedgerRepository ledgerRepository;


    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public boolean saveLedgerAndUpdateLedgerUpdated(LedgerDTO ledgerDTO, boolean isCancelled) {
        ledgerRepository.saveAll(List.of(
                Ledger.builder().transactionType(isCancelled ? LedgerTransactionStatus.CANCEL_DEBIT:LedgerTransactionStatus.DEBIT ).userId(ledgerDTO.getSellerId()).bookingId(ledgerDTO.getBookingId())
                        .amount(ledgerDTO.getAmount()).currency(ledgerDTO.getCurrency()).paymentId(ledgerDTO.getPaymentId()).txId(ledgerDTO.getTxId()).paymentType(ledgerDTO.getPaymentType())
                        .build(),
                Ledger.builder().transactionType(isCancelled ? LedgerTransactionStatus.CANCEL_CREDIT:LedgerTransactionStatus.CREDIT).userId(ledgerDTO.getBuyerId()).bookingId(ledgerDTO.getBookingId())
                        .amount(ledgerDTO.getAmount()).currency(ledgerDTO.getCurrency()).paymentId(ledgerDTO.getPaymentId()).txId(ledgerDTO.getTxId()).paymentType(ledgerDTO.getPaymentType())
                        .build()
        ));
        return true;
    }
}
