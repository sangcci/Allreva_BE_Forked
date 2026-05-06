package com.backend.allreva.common.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StopWatch;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class HttpLogFilter extends OncePerRequestFilter {

    private static final String CORRELATION_ID = "correlationId";
    private static final AntPathMatcher pathMatcher = new AntPathMatcher();
    private static final ArrayList<String> loggingExcludeUrls =
            new ArrayList<>(List.of("/actuator/**", "/swagger/**", "/swagger-ui/**", "/api-docs/**", "/favicon.ico"));
    private static final List<String> queryStringExcludeUrls = List.of("/api/v1/auth/**");

    @Override
    protected boolean shouldNotFilter(final HttpServletRequest request) throws ServletException {
        return loggingExcludeUrls.stream().anyMatch(uri -> pathMatcher.match(uri, request.getRequestURI()));
    }

    @Override
    protected void doFilterInternal(
            final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain)
            throws ServletException, IOException {
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        if (MDC.get(CORRELATION_ID) == null) {
            MDC.put(CORRELATION_ID, generateShortId());
        }
        MDC.put("user_id", getUserIdFromAuthentication());

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            stopWatch.stop();

            String method = requestWrapper.getMethod();
            String uri = requestWrapper.getRequestURI();
            String status = String.valueOf(responseWrapper.getStatus());
            String duration = String.valueOf(stopWatch.getTotalTimeMillis());
            String clientIp = getClientIp(request);

            boolean excludeQueryString =
                    queryStringExcludeUrls.stream().anyMatch(pattern -> pathMatcher.match(pattern, uri));
            String queryString = excludeQueryString ? null : requestWrapper.getQueryString();

            MDC.put("method", method);
            MDC.put("uri", uri);
            if (queryString != null) {
                MDC.put("query_string", queryString);
            }
            MDC.put("status", status);
            MDC.put("duration_ms", duration);
            MDC.put("client_ip", clientIp);

            log.info(
                    "HTTP {} {}{} → {} ({}ms) ip={}",
                    method,
                    uri,
                    queryString != null ? "?" + queryString : "",
                    status,
                    duration,
                    clientIp);

            responseWrapper.copyBodyToResponse();
            MDC.clear();
        }
    }

    private String generateShortId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    private String getClientIp(final HttpServletRequest request) {
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isBlank()) {
            return xRealIp;
        }
        return request.getRemoteAddr();
    }

    public static String getUserIdFromAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (authentication != null && authentication.isAuthenticated()) ? authentication.getName() : "ANONYMOUS";
    }
}
