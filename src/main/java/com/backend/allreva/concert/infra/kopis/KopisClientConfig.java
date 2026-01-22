package com.backend.allreva.concert.infra.kopis;

import org.springframework.context.annotation.Bean;

import feign.codec.Decoder;
import feign.jaxb.JAXBContextFactory;
import feign.jaxb.JAXBDecoder;

/**
 * Kopis API Feign Client 설정
 *
 * Kopis API는 XML 응답을 반환하므로 JAXB Decoder를 사용합니다.
 */
public class KopisClientConfig {

    /**
     * XML 응답을 Java 객체로 변환하는 Decoder
     *
     * @return JAXB Decoder (UTF-8 인코딩)
     */
    @Bean
    public Decoder xmlDecoder() {
        return new JAXBDecoder(new JAXBContextFactory.Builder()
                .withMarshallerJAXBEncoding("UTF-8")
                .build());
    }
}
