package com.backend.allreva.common.config;

import feign.RequestInterceptor;
import feign.jaxb.JAXBContextFactory;
import feign.jaxb.JAXBDecoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class KopisFeignConfig {

    @Bean
    public JAXBDecoder xmlDecoder() {
        return new JAXBDecoder(new JAXBContextFactory.Builder()
                .withMarshallerJAXBEncoding("UTF-8")
                .build());
    }

    @Bean
    public RequestInterceptor kopisServiceKeyInterceptor(@Value("${public-data.kopis.service-key}") String serviceKey) {
        return requestTemplate -> requestTemplate.query("service", serviceKey);
    }
}
