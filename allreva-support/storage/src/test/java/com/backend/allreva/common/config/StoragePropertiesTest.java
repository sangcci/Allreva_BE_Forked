package com.backend.allreva.common.config;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("StorageProperties 단위 테스트")
class StoragePropertiesTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(TestConfig.class)
            .withPropertyValues(
                    "spring.cloud.aws.s3.bucket=test-bucket",
                    "spring.cloud.aws.credentials.access-key=access-key",
                    "spring.cloud.aws.credentials.secret-key=secret-key",
                    "spring.cloud.aws.region.static=ap-northeast-2");

    @Test
    void S3_설정을_바인딩한다() {
        contextRunner.run(context -> {
            StorageProperties properties = context.getBean(StorageProperties.class);

            assertSoftly(softly -> {
                softly.assertThat(properties.s3().bucket()).isEqualTo("test-bucket");
                softly.assertThat(properties.credentials().accessKey()).isEqualTo("access-key");
                softly.assertThat(properties.credentials().secretKey()).isEqualTo("secret-key");
                softly.assertThat(properties.region().staticRegion()).isEqualTo("ap-northeast-2");
            });
        });
    }

    @EnableConfigurationProperties(StorageProperties.class)
    static class TestConfig {}
}
