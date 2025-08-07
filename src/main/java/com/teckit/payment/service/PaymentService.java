package com.teckit.payment.service;

import com.teckit.payment.dto.request.PaymentEventDTO;
import com.teckit.payment.repository.PaymentEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentEventRepository paymentEventRepository;

    private final RestClient portOneRestClient;

    @Value("${portone.api.key}")
    private String portOneAPIKey;

    @Value("${portone.channel.key}")
    private String portOneChannelKey;

    @Value("${portone.store.id}")
    private String portOneStoreId;


    public void requestPayment(PaymentEventDTO dto){

    }
}
