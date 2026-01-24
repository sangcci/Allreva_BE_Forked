package com.backend.allreva.common.config;

import feign.jaxb.JAXBContextFactory;
import feign.jaxb.JAXBDecoder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.backend.allreva.module")
public class OpenFeignConfig {

    public static class ClientXmlDecoder {
        @Bean
        public JAXBDecoder xmlDecoder() {
            return new JAXBDecoder(new JAXBContextFactory.Builder()
                    .withMarshallerJAXBEncoding("UTF-8")
                    .build());
        }
    }
}
