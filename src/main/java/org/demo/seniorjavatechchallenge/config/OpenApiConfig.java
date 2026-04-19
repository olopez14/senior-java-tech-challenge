package org.demo.seniorjavatechchallenge.config;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Products & Prices API")
                        .version("0.0.1")
                        .description("API para gestionar productos y precios históricos. Implementación con énfasis en rendimiento y caché Caffeine.")
                        .contact(new Contact().name("Developer").email("dev@example.com"))
                        .license(new License().name("MIT")));
    }
}

