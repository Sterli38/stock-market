package com.example.stockmarket.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "Stock market",
                description = "Stock market", version = "1.0.0",
                contact = @Contact(
                        name = "Velikanov Leonid",
                        email = "lenya.vel@mail.ru"
                )
        )
)
public class SwaggerConfig {
}
