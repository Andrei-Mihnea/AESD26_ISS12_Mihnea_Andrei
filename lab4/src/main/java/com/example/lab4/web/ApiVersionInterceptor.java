package com.example.lab4.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.Instant;

/** Response metadata for the explicitly mapped URI versions; never rewrites or retries a request. */
public class ApiVersionInterceptor implements HandlerInterceptor {
    public static final String VERSION_ATTRIBUTE = ApiVersionInterceptor.class.getName() + ".version";
    private static final String DEPRECATION_DATE = "@" + Instant.parse("2026-10-31T00:00:00Z").getEpochSecond();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Object pattern = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        if (pattern == null || !pattern.toString().startsWith("/api/")) return true;
        String version = pattern.toString().startsWith("/api/v2/") ? "2" : "1";
        request.setAttribute(VERSION_ATTRIBUTE, version);
        response.setHeader("API-Version", version);
        if (version.equals("1")) {
            response.setHeader("Deprecation", DEPRECATION_DATE);
            String prefix = pattern.toString().startsWith("/api/v1/") ? "/api/v1/" : "/api/";
            String uri = request.getRequestURI();
            // Search after the context path so contexts such as /api/demo remain intact.
            int start = uri.indexOf(prefix, request.getContextPath().length());
            if (start >= 0) {
                String path = uri.substring(0, start) + "/api/v2/" + uri.substring(start + prefix.length());
                String successor = ServletUriComponentsBuilder.fromCurrentRequest().replacePath(path)
                        .build().toUriString();
                response.addHeader("Link", "<" + successor + ">; rel=\"successor-version\"");
            }
        }
        return true;
    }
}
