package com.project.youtlix.common.infrastructure.in.web;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI youtlixOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Youtlix API")
                        .description("REST API for the Youtlix modular monolith")
                        .version("0.0.1"));
    }
}
