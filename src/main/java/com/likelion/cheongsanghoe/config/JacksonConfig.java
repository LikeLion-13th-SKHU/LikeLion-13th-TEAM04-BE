package com.likelion.cheongsanghoe.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        // Java 8의 LocalDateTime 타입을 JSON으로 변환해주는 모듈을 등록합니다.
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }
}
