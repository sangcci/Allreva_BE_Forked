package com.backend.allreva.common.config;

import com.backend.allreva.concert.concert.kopis.KopisConcertClient;
import com.backend.allreva.concert.place.kopis.KopisHallClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(clients = {KopisConcertClient.class, KopisHallClient.class})
@EnableConfigurationProperties(KopisProperties.class)
public class OpenFeignConfig {}
