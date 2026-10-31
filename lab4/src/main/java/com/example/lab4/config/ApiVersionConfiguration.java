package com.example.lab4.config;

import com.example.lab4.web.ApiVersionInterceptor;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ApiVersionConfiguration implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ApiVersionInterceptor()).addPathPatterns("/api/**");
    }

    @Bean
    public GroupedOpenApi v1Api() {
        return GroupedOpenApi.builder().group("v1").pathsToMatch("/api/v1/**")
                .addOpenApiCustomizer(api -> {
                    api.info(new Info().title("Routing API v1").version("1")
                            .description("Deprecated but supported. Prefer v2; see README migration strategy."));
                    api.getPaths().values().forEach(path -> path.readOperations()
                            .forEach(operation -> operation.setDeprecated(true)));
                })
                .build();
    }

    @Bean
    public GroupedOpenApi v2Api() {
        return GroupedOpenApi.builder().group("v2").pathsToMatch("/api/v2/**")
                .addOpenApiCustomizer(api -> api.info(new Info().title("Routing API v2").version("2")
                        .description("Preferred URI version. Currently uses the same DTO contract as v1.")))
                .build();
    }
}
