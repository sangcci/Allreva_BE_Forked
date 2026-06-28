package com.backend.allreva.support;

import com.backend.allreva.ApiServerApplication;
import com.backend.allreva.auth.kakao.KakaoAuthClient;
import com.backend.allreva.auth.kakao.KakaoUserInfoClient;
import com.backend.allreva.common.config.JpaAuditingConfig;
import com.backend.allreva.common.storage.StorageWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(classes = ApiServerApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureWireMock(port = 0)
@Import({FixedClockConfig.class})
public abstract class IntegrationTestSupport {

    private static final String S3_BUCKET = "test-bucket";

    @ServiceConnection
    @SuppressWarnings("resource")
    protected static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @ServiceConnection(name = "redis")
    @SuppressWarnings("resource")
    protected static final GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine").withExposedPorts(6379);

    @SuppressWarnings("resource")
    protected static final LocalStackContainer localStack = new LocalStackContainer(
                    DockerImageName.parse("localstack/localstack:3.8.1"))
            .withServices(LocalStackContainer.Service.S3);

    static {
        postgres.start();
        redis.start();
        localStack.start();
        createS3Bucket();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> postgres.getJdbcUrl() + "?stringtype=unspecified");
        registry.add("spring.cloud.aws.s3.bucket", () -> S3_BUCKET);
        registry.add("spring.cloud.aws.s3.endpoint", () -> localStack
                .getEndpointOverride(LocalStackContainer.Service.S3)
                .toString());
        registry.add("spring.cloud.aws.credentials.access-key", localStack::getAccessKey);
        registry.add("spring.cloud.aws.credentials.secret-key", localStack::getSecretKey);
        registry.add("spring.cloud.aws.region.static", localStack::getRegion);
    }

    private static void createS3Bucket() {
        try {
            localStack.execInContainer("awslocal", "s3", "mb", "s3://" + S3_BUCKET);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to create LocalStack S3 bucket", e);
        }
    }

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @MockBean
    protected JpaAuditingConfig jpaAuditingConfig;

    @SpyBean
    protected StorageWriter storageWriter;

    @MockBean
    protected KakaoAuthClient kakaoAuthClient;

    @MockBean
    protected KakaoUserInfoClient kakaoUserInfoClient;
}
