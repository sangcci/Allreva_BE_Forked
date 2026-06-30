package com.backend.allreva.auth;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenCookieWriter {

    @Value("${auth.jwt.refresh-token.expiration}")
    private int refreshTime;

    @Value("${auth.jwt.refresh-token.name}")
    private String cookieName;

    @Value("${auth.jwt.refresh-token.cookie.domain}")
    private String cookieDomain;

    @Value("${auth.jwt.refresh-token.cookie.secure}")
    private boolean secure;

    @Value("${auth.jwt.refresh-token.cookie.same-site}")
    private String sameSite;

    public void write(final HttpServletResponse response, final String refreshTokenJwt) {
        response.addHeader(
                HttpHeaders.SET_COOKIE,
                buildCookie(cookieName, refreshTokenJwt, cookieDomain, refreshTime)
                        .toString());
    }

    public void delete(final HttpServletResponse response) {
        response.addHeader(
                HttpHeaders.SET_COOKIE,
                buildCookie(cookieName, "", cookieDomain, 0).toString());
    }

    private ResponseCookie buildCookie(final String name, final String value, final String domain, final int maxAge) {
        return ResponseCookie.from(name, value)
                .domain(domain)
                .path("/")
                .maxAge(maxAge)
                .httpOnly(true)
                .secure(secure)
                .sameSite(sameSite)
                .build();
    }
}
