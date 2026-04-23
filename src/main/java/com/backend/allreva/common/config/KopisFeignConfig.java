package com.backend.allreva.common.config;

import feign.jaxb.JAXBContextFactory;
import feign.jaxb.JAXBDecoder;
import org.springframework.context.annotation.Bean;

public class KopisFeignConfig {

    @Bean
    public JAXBDecoder xmlDecoder() {
        return new JAXBDecoder(new JAXBContextFactory.Builder()
                .withMarshallerJAXBEncoding("UTF-8")
                .build());
    }
}
