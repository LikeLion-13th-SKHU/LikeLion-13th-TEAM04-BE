package com.likelion.cheongsanghoe.config;

import com.likelion.cheongsanghoe.global.properties.SwaggerProperties;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.ArrayList;

@Configuration
@EnableWebMvc
public class SwaggerConfig {

    private final SwaggerProperties swaggerProperties;

    @Autowired
    public SwaggerConfig(MappingJackson2HttpMessageConverter converter, SwaggerProperties swaggerProperties) {
        this.swaggerProperties = swaggerProperties;
        var supportedMediaTypes = new ArrayList<>(converter.getSupportedMediaTypes());
        supportedMediaTypes.add(new MediaType("application", "octet-stream"));
        converter.setSupportedMediaTypes(supportedMediaTypes);
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Cheongsanghoe API")
                        .description("청상회 프로젝트의 REST API 명세서입니다.")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Cheongsanghoe Team")
                                .email("wjdekdns0218@gmail.com"))
                        .license(new License()
                                .name("Apache 2.0")));
    }

    private Server server(){
        String url = swaggerProperties.url();
        String description = url.contains("localhost") ? "Local Server" : "Dec Server";

        return new Server()
                .url(url)
                .description(description);
    }
}
