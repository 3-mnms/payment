package com.teckit.payment.dto.response;

import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class PaymentCancelResponseDTO {
    private CancellationDTO cancellation;
}
