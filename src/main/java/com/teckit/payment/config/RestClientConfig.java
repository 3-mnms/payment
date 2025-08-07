package com.teckit.payment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient portOneRequestPayment(){
        return RestClient.builder()
                .baseUrl("https://checkout.portone.io/v2/payment")
                .build();
    }
}
