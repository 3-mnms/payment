package com.teckit.payment.dto.response;


import com.teckit.payment.entity.TekcitPayAccount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TekcitPayAccountResponseDTO {
    private Long availableBalance;
    private LocalDateTime updatedAt;

    public static TekcitPayAccountResponseDTO fromEntity(TekcitPayAccount tekcitPayAccount) {
        return TekcitPayAccountResponseDTO.builder()
                .availableBalance(tekcitPayAccount.getAvailableBalance())
                .updatedAt(tekcitPayAccount.getUpdatedAt())
                .build();
    }
}
