package com.teckit.payment.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.teckit.payment.enumeration.PayMethodType;
import com.teckit.payment.enumeration.PaymentOrderStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDTO {
//    결제 번호
    @NotBlank
    private String paymentId;

//    예매 번호
    @NotBlank
    private String bookingId;

    //공연 정보
    private String festivalId;

    //    결제 상태 : 요청, 준비, 완료, 취소, 거부(신용 불량 등), 실패 (잔액 없음 등)
//    실패와 거부는 동일하게 ?
    @NotNull
    private PaymentOrderStatus paymentRequestType;

    //    양도에서는 피양도자가 buyerId
    //    예매에서는 예약자가 buyerId;
    private Long buyerId;

    //    양도에서는 양도자가 sellerId
    //    예매에서는 주최자가 sellerId
    private Long sellerId;

    //    가격
    @NotNull
    private Long amount;

    //
    private String currency="KRW";

    //    외부 입력
    @NotNull
    @JsonProperty("payMethod")
    private PayMethodType payMethod;
}
