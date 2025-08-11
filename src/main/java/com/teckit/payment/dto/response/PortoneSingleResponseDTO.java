package com.teckit.payment.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PortoneSingleResponseDTO {
    private String status;

    @JsonProperty("id")
    private String paymentId;

    @JsonProperty("transactionId")
    private String txId;
    private String merchantId;
    private String storeId;
    private String orderName;
    private String currency;
    private String paidAt;
    private String pgTxId;

    private Amount amount;

    @Data @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Amount {
        private Long total;        // 핵심: 총 결제 금액
        private Long discount;
        // 필요한 경우만 추가: paid, cancelled 등
        private Long paid;
        private Long cancelled;
    }



}
