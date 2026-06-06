package com.backend.allreva.auth.kakao;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oauth2.kakao")
public record KakaoOAuthProperties(String redirectUri, String clientId, String clientSecret) {}
