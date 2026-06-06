package com.backend.allreva.support;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import com.backend.allreva.auth.kakao.KakaoOAuthClientConfig;
import com.backend.allreva.auth.kakao.KakaoOAuthIdentityVerifier;
import com.backend.allreva.auth.kakao.KakaoOAuthMemberMapper;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@ContextConfiguration(
        classes = {KakaoOAuthClientConfig.class, KakaoOAuthIdentityVerifier.class, KakaoOAuthMemberMapper.class})
@ImportAutoConfiguration({
    FeignAutoConfiguration.class,
    HttpMessageConvertersAutoConfiguration.class,
    JacksonAutoConfiguration.class
})
public abstract class OAuthClientTestSupport {

    @RegisterExtension
    static final WireMockExtension wireMock = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .configureStaticDsl(true)
            .build();

    @DynamicPropertySource
    static void configureProperties(final DynamicPropertyRegistry registry) {
        registry.add("oauth2.kakao.auth-url", wireMock::baseUrl);
        registry.add("oauth2.kakao.api-url", wireMock::baseUrl);
        registry.add("oauth2.kakao.redirect-uri", () -> "http://localhost/callback");
        registry.add("oauth2.kakao.client-id", () -> "client-id");
        registry.add("oauth2.kakao.client-secret", () -> "client-secret");
    }
}
