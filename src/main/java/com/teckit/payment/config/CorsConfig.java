package com.teckit.payment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class CorsConfig {
//    @Bean
//    public CorsFilter corsFilter() {
//        CorsConfiguration config = new CorsConfiguration();
//
//        // 프론트엔드 도메인 명시
//        config.setAllowedOrigins(List.of("http://localhost:10000"));
//
//        // 허용 메서드
//        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//
//        // 허용 헤더
//        config.setAllowedHeaders(List.of("*"));
//
//        // 인증정보 포함 여부 (사용 안 한다 했으므로 false 또는 생략 가능)
//        config.setAllowCredentials(false);
//
//        // CORS 설정을 적용할 경로
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", config);
//
//        return new CorsFilter(source);
//    }
}
