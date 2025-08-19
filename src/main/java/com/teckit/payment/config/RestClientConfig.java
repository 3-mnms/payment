package com.teckit.payment.config;

import com.teckit.payment.exception.BusinessException;
import com.teckit.payment.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;

@Configuration
@Slf4j
public class RestClientConfig {

    @Value("${portone.api.secret-key}")
    private String apiKey;

    @Bean
    public RestClient PortOneClient(){
        log.info("✅ Portone api key {}",apiKey);
        return RestClient.builder()
                .baseUrl("https://api.portone.io")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "PortOne "+apiKey)
                .build();
    }
}
