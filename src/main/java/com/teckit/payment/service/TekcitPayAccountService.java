package com.teckit.payment.service;


import com.teckit.payment.dto.response.TekcitPayAccountResponseDTO;
import com.teckit.payment.entity.TekcitPayAccount;
import com.teckit.payment.exception.BusinessException;
import com.teckit.payment.exception.ErrorCode;
import com.teckit.payment.repository.TekcitPayAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TekcitPayAccountService {
    private final TekcitPayAccountRepository tekcitPayAccountRepository;

    public TekcitPayAccountResponseDTO getTekcitPayAccountById(Long id) {
        TekcitPayAccount tekcitPayAccount = tekcitPayAccountRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_TEKCIT_PAY_ACCOUNT));
        return TekcitPayAccountResponseDTO.fromEntity(tekcitPayAccount);
    }

    @Transactional
    public void createTekcitPayAccount(Long id) {
        tekcitPayAccountRepository.save(TekcitPayAccount.builder()
                .userId(id)
                .availableBalance(0L)
                .build());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void increaseAvailableBalance(Long userId, Long chargedAmount) {
        TekcitPayAccount tekcitPayAccount = tekcitPayAccountRepository.findByIdForUpdate(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_TEKCIT_PAY_ACCOUNT));
        tekcitPayAccount.setAvailableBalance(Math.addExact(tekcitPayAccount.getAvailableBalance(), chargedAmount));
        tekcitPayAccountRepository.save(tekcitPayAccount);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void decreaseAvailableBalance(Long userId, Long chargedAmount) {
        TekcitPayAccount tekcitPayAccount = tekcitPayAccountRepository.findByIdForUpdate(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_TEKCIT_PAY_ACCOUNT));

        tekcitPayAccount.setAvailableBalance(tekcitPayAccount.getAvailableBalance() - chargedAmount);
        tekcitPayAccountRepository.save(tekcitPayAccount);
    }


}
