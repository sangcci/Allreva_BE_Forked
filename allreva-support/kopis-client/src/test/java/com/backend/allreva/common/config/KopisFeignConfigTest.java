package com.backend.allreva.common.config;

import static org.assertj.core.api.Assertions.assertThat;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("KopisFeignConfig 단위 테스트")
class KopisFeignConfigTest {

    private final KopisFeignConfig config = new KopisFeignConfig();

    @Nested
    @DisplayName("kopisServiceKeyInterceptor 메서드는")
    class Describe_kopisServiceKeyInterceptor {

        @Test
        void KOPIS_service_key를_query_parameter로_추가한다() {
            RequestInterceptor interceptor = config.kopisServiceKeyInterceptor(
                    new KopisProperties("https://www.kopis.or.kr/openApi/restful", "service-key"));
            RequestTemplate requestTemplate = new RequestTemplate();

            interceptor.apply(requestTemplate);

            assertThat(requestTemplate.queries()).containsKey("service");
            assertThat(requestTemplate.queries().get("service")).containsExactly("service-key");
        }
    }
}
