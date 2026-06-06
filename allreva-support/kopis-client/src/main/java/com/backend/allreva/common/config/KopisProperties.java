package com.backend.allreva.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "public-data.kopis")
public record KopisProperties(String baseUrl, String serviceKey) {}
