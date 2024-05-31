package net.grayfallstown.chatgptfileandcommandcenter.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("ChatGPT File and Command Center API").version("1.0"))
                .addSecurityItem(new SecurityRequirement().addList("apiKey"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("apiKey", new SecurityScheme()
                                .name("Authorization")
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .bearerFormat("JWT")
                                .scheme("bearer")));
    }
}
