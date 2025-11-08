package com.allra.backend.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        OpenAPI openAPI = new OpenAPI()
            .info(new Info()
                .title("🚀 Allra Backend API")
                .description("""
                    <b>올라 핀테크 백엔드 과제 API 명세서</b><br>
                    <i>Spring Boot + JPA + Swagger(OpenAPI 3.0)</i><br>
                    <span style='color:#007ACC;'>모든 주요 기능(User, Cart, Product) 엔드포인트 포함</span>
                    """)
                .version("1.0.0")
            );
            
        return openAPI;
    }
}
