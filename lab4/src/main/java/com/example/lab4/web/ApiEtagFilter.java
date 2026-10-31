package com.example.lab4.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ETag;
import org.springframework.http.HttpHeaders;
import org.springframework.util.DigestUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.Collections;

/** Conditional reads based on the actual serialized API representation. */
public class ApiEtagFilter extends OncePerRequestFilter {
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !"GET".equals(request.getMethod());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        ContentCachingResponseWrapper cached = new ContentCachingResponseWrapper(response);
        // An intermediary cache must distinguish JSON and XML representations.
        cached.addHeader(HttpHeaders.VARY, HttpHeaders.ACCEPT);
        chain.doFilter(request, cached);

        String cacheControl = cached.getHeader(HttpHeaders.CACHE_CONTROL);
        if (cached.getStatus() == HttpServletResponse.SC_OK && !cached.isCommitted()
                && (cacheControl == null || !cacheControl.contains("no-store"))) {
            ETag current = new ETag(DigestUtils.md5DigestAsHex(cached.getContentAsByteArray()), false);
            cached.setHeader(HttpHeaders.ETAG, current.formattedTag());
            boolean matches = Collections.list(request.getHeaders(HttpHeaders.IF_NONE_MATCH)).stream()
                    .flatMap(value -> ETag.parse(value).stream())
                    .anyMatch(candidate -> candidate.isWildcard() || candidate.compare(current, false));
            if (matches) {
                cached.resetBuffer();
                cached.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                cached.setHeader(HttpHeaders.CONTENT_LENGTH, null);
            }
        }
        cached.copyBodyToResponse();
    }
}
