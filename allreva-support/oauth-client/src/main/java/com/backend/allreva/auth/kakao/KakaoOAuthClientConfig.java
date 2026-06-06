package com.backend.allreva.auth.kakao;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(clients = {KakaoAuthClient.class, KakaoUserInfoClient.class})
@EnableConfigurationProperties(KakaoOAuthProperties.class)
public class KakaoOAuthClientConfig {}
