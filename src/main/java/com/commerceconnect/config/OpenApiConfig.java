package com.commerceconnect.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI commerceConnectOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("CommerceConnect API")
                        .description("Enterprise commerce and marketing platform API for portfolio and Deloitte-aligned engineering assessment.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("CommerceConnect Team")
                                .email("support@commerceconnect.example"))
                        .license(new License().name("MIT License").url("https://opensource.org/licenses/MIT"))
                );
    }
}
