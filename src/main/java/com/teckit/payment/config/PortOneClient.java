package com.teckit.payment.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teckit.payment.dto.request.PaymentCancelRequestDTO;

import com.teckit.payment.dto.response.PaymentCancelResponseDTO;
import com.teckit.payment.dto.response.PortoneSingleResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class PortOneClient {

    private final ObjectMapper objectMapper;

    @Qualifier("PortOneClient")
    private final RestClient portOneClient;

    public PortoneSingleResponseDTO getPayment(String paymentId){
        return portOneClient.get()
                .uri("/payments/{paymentId}", paymentId)  // 실제 스펙에 맞게 수정
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    JsonNode root = objectMapper.readTree(res.getBody());
                    String type = root.path("type").asText("");       // "UNAUTHORIZED" 등

                    throw switch (type) {
                        case "INVALID_REQUEST"   -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request to PortOne");
                        case "UNAUTHORIZED"      -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized request to PortOne");
                        case "FORBIDDEN"         -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden request to PortOne");
                        case "PAYMENT_NOT_FOUND" -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment ID not found in PortOne");
                        default                  -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "External API error from PortOne");
                    };
                })
                .body(PortoneSingleResponseDTO.class);
    }

    public PaymentCancelResponseDTO cancelPayment(String paymentId){
        PaymentCancelRequestDTO dto= PaymentCancelRequestDTO.builder()
                .reason("사용자 요청으로 인한 환불")
                .build();

        return portOneClient.post()
                .uri("/payments/{paymentId}/cancel", paymentId)
                .body(dto)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {})
                .body(PaymentCancelResponseDTO.class);
    }
}
