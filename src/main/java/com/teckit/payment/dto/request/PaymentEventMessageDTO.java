package com.teckit.payment.dto.request;

import com.teckit.payment.enumeration.PaymentOrderStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PaymentEventMessageDTO {
//    무작위로 발생된 주문 번호
    @NotBlank
    private String paymentId;

    @NotBlank
    private String bookingId;

//    공연 정보
    @NotBlank
    private String festivalId;

//    결제 상태 : 요청, 준비, 완료, 취소, 거부(신용 불량 등), 실패 (잔액 없음 등)
//    실패와 거부는 동일하게 ?
    @NotNull
    private PaymentOrderStatus eventType;

//    양도에서는 피양도자가 buyerId
//    예매에서는 예약자가 buyerId;
    @NotNull
    private Long buyerId;

//    양도에서는 양도자가 sellerId
//    예매에서는 주최자가 sellerId
    @NotNull
    private Long sellerId;

//    가격
    @NotNull
    private Long amount;

    private String currency="KRW";

//    외부 입력
    @NotBlank
    private String payMethod;
}
