package com.likelion.cheongsanghoe.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Cheongsanghoe API",
                description = "Cheongsanghoe 백엔드 API 문서"
        )
)
public class OpenApiConfig {
}
