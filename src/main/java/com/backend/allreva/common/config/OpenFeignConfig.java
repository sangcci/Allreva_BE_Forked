package com.backend.allreva.common.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.backend.allreva.module")
public class OpenFeignConfig {}
