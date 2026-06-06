package com.backend.allreva.auth;

import jakarta.servlet.http.HttpServletResponse;
import java.net.MalformedURLException;
import java.net.URL;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenCookieWriter {

    @Value("${jwt.refresh.expiration}")
    private int refreshTime;

    @Value("${url.front.domain-name}")
    private String prodDomainName;

    public void write(final HttpServletResponse response, final String refreshToken, final String domainName) {
        String cookieDomain = isLocalhost(domainName) ? null : prodDomainName;
        response.addHeader(
                HttpHeaders.SET_COOKIE,
                buildCookie("refreshToken", refreshToken, cookieDomain, refreshTime)
                        .toString());
    }

    public void delete(final HttpServletResponse response, final String domainName) {
        String cookieDomain = isLocalhost(domainName) ? null : prodDomainName;
        response.addHeader(
                HttpHeaders.SET_COOKIE,
                buildCookie("refreshToken", "", cookieDomain, 0).toString());
    }

    private static ResponseCookie buildCookie(
            final String name, final String value, final String domain, final int maxAge) {
        return ResponseCookie.from(name, value)
                .domain(domain)
                .path("/")
                .maxAge(maxAge)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .build();
    }

    private static boolean isLocalhost(String domain) {
        if (domain == null) {
            return false;
        }
        try {
            URL url = new URL(domain);
            String host = url.getHost();
            return host.contains("localhost") || host.contains("127.0.0.1");
        } catch (MalformedURLException e) {
            return false;
        }
    }
}
