package com.example.lab1;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.logging.Logger;

@WebFilter("/*")
public class RequestLoggingFilter implements Filter {
    private static final Logger LOGGER =
            Logger.getLogger(RequestLoggingFilter.class.getName());


    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String method = httpRequest.getMethod();
        String ip = httpRequest.getRemoteAddr();
        String userAgent = httpRequest.getHeader("User-Agent");
        String languages = httpRequest.getHeader("Accept-Language");
        String page = httpRequest.getParameter("page");

        String message = String.format(
                "method=%s ip=%s userAgent=%s languages=%s page=%s",
                method,
                ip,
                safe(userAgent),
                safe(languages),
                safe(page)
        );

        LOGGER.info(message);
        httpRequest.getServletContext().log(message);

        chain.doFilter(request, response);
    }

    private String safe(String value) {
        if (value == null) {
            return "(not supplied)";
        }

        return value.replace('\r', ' ').replace('\n', ' ');
    }
}