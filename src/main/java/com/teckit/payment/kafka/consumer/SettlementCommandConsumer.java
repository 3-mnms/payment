package com.teckit.payment.kafka.consumer;

import com.teckit.payment.dto.request.SettlementCommandDTO;
import com.teckit.payment.service.PaymentOrchestrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SettlementCommandConsumer {
    private final PaymentOrchestrationService paymentOrchestrationService;

    @KafkaListener(
            topics = "${app.kafka.topic.settlement-command}",
            groupId="settlement-consumer"
    )
//    DLQ 처리
    public void consume(SettlementCommandDTO dto){
        log.info("😡Settlement 이벤트 발생");

        paymentOrchestrationService.handleSettlement(dto);
    }
}
