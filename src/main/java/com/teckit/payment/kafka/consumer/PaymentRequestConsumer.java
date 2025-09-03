package com.teckit.payment.kafka.consumer;


import com.teckit.payment.dto.request.PaymentEventMessageDTO;
import com.teckit.payment.dto.request.PaymentRequestDTO;
import com.teckit.payment.enumeration.PaymentOrderStatus;
import com.teckit.payment.service.PaymentEventService;
import com.teckit.payment.service.PaymentOrderService;
import com.teckit.payment.util.PaymentOrderStatusUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentRequestConsumer {
    private final PaymentEventService paymentEventService;
    private final PaymentOrderService paymentOrderService;

    @KafkaListener(
            topics = "${app.kafka.topic.payment-request}",
            groupId="payment-request"
    )
    @Transactional
    public void consume(PaymentRequestDTO dto) {
        //        sellerId와 buyerId, 즉 자신에게 양도 혹은 자신의 상품을 구매하는 것은 block
//        PaymentRequest -> PaymentEvent

        log.info("PaymentRequest 수신 완료 : {}",dto.toString());
        PaymentEventMessageDTO event = PaymentEventMessageDTO.fromPaymentRequest(dto);

        if (dto.getPaymentRequestType() == PaymentOrderStatus.POINT_CHARGE_REQUESTED) {
            event.setSellerId(1L);
        }

        if (isBuyerIdEqualsSellerId(dto)) {
            PaymentOrderStatus status= PaymentOrderStatusUtil.withPhase(dto.getPaymentRequestType(), "REJECTED");
            event.setEventType(status);
            paymentEventService.savePaymentEvent(event);
            return;
        }



        paymentEventService.savePaymentEvent(event);
//        PaymentOrder 초기 생성
//        txId는 webhook이 Ready일 때 저장
        paymentOrderService.findOrCreateFromEvent(event);
    }

    private static boolean isBuyerIdEqualsSellerId(PaymentRequestDTO dto) {
        return Objects.equals(dto.getSellerId(), dto.getBuyerId());
    }
}
