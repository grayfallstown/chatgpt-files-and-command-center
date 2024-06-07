package net.grayfallstown.chatgptfileandcommandcenter.common;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.PathItem;

@Configuration
public class OpenApiConfig {
    private static final String X_OPENAI_IS_CONSEQUENTIAL = "x-openai-isConsequential";

    @Bean
    public OpenApiCustomizer customOpenApiCustomiser() {
        return openApi -> {
            for (PathItem pathitem : openApi.getPaths().values()) {
                pathitem.addExtension(X_OPENAI_IS_CONSEQUENTIAL, false);
                if (pathitem.getGet() != null)
                    pathitem.getGet().addExtension(X_OPENAI_IS_CONSEQUENTIAL, false);
                if (pathitem.getPost() != null)
                    pathitem.getPost().addExtension(X_OPENAI_IS_CONSEQUENTIAL, false);
            }
            openApi.addExtension(X_OPENAI_IS_CONSEQUENTIAL, false);
            openApi.addExtension31(X_OPENAI_IS_CONSEQUENTIAL, false);
        };
    }

}
