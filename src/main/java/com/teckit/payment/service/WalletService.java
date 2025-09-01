package com.teckit.payment.service;


import com.teckit.payment.dto.request.WalletDTO;
import com.teckit.payment.entity.PaymentOrder;
import com.teckit.payment.entity.Wallet;
import com.teckit.payment.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public boolean saveAndUpdateWallet(WalletDTO walletDTO) {
        Wallet buyer = walletRepository.findById(walletDTO.getBuyerId())
                .orElseGet(() -> new Wallet(walletDTO.getBuyerId()));
        Wallet seller = walletRepository.findById(walletDTO.getSellerId())
                .orElseGet(() -> new Wallet(walletDTO.getSellerId()));

        buyer.setTotalPaidAmount(nz(buyer.getTotalPaidAmount()) + walletDTO.getAmount());
        seller.setTotalReceivedAmount(nz(seller.getTotalReceivedAmount()) + walletDTO.getAmount());

        walletRepository.save(buyer);
        walletRepository.save(seller);

        return true;
    }

    private long nz(Long v) {
        return v == null ? 0L : v;
    }

}
