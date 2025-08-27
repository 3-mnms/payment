package com.teckit.payment.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class PaymentCancellationService {
    private Long availableBalance;
    private LocalDateTime updatedAt;
}
