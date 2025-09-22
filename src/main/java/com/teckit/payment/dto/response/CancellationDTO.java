package com.teckit.payment.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
@Data
public class CancellationDTO {
    private String status;
    private String id;
    private String pgCancellationId;
    private Long totalAmount;
    private Long taxFreeAmount;
    private Long vatAmount;
    private String reason;
    private String trigger;
    private String receiptUrl;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private ZonedDateTime requestedAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private ZonedDateTime cancelledAt;
}