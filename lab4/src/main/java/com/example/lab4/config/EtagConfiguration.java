package com.example.lab4.config;

import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.example.lab4.web.ApiEtagFilter;

@Configuration
public class EtagConfiguration {
    @Bean
    public FilterRegistrationBean<ApiEtagFilter> apiEtagFilter() {
        // Hash the serialized GET response; the filter handles If-None-Match comparison and 304 responses.
        FilterRegistrationBean<ApiEtagFilter> registration =
                new FilterRegistrationBean<>(new ApiEtagFilter());
        registration.addUrlPatterns("/api/*");
        return registration;
    }

    @Bean
    public OpenApiCustomizer conditionalGetDocumentation() {
        return openApi -> {
            if (openApi.getPaths() == null) return;
            openApi.getPaths().forEach((path, item) -> {
                if (!path.startsWith("/api/") || item.getGet() == null) return;
                var operation = item.getGet();
                operation.addParametersItem(new Parameter()
                        .name("If-None-Match").in("header").required(false)
                        .description("ETag from a previous GET, including quotes. A match returns 304 with no body.")
                        .schema(new StringSchema()));
                var etagHeader = new Header().description("Validator for the current negotiated representation")
                        .schema(new StringSchema());
                var ok = operation.getResponses().get("200");
                if (ok != null) ok.addHeaderObject("ETag", etagHeader);
                operation.getResponses().addApiResponse("304", new ApiResponse()
                        .description("Not Modified: use the previously received representation")
                        .addHeaderObject("ETag", etagHeader));
            });
        };
    }
}
