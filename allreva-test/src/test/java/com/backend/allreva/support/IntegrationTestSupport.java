package com.backend.allreva.support;

import com.backend.allreva.ApiServerApplication;
import com.backend.allreva.auth.kakao.KakaoAuthClient;
import com.backend.allreva.auth.kakao.KakaoUserInfoClient;
import com.backend.allreva.common.config.JpaAuditingConfig;
import com.backend.allreva.common.storage.StorageWriter;
import com.backend.allreva.notification.fcm.FcmInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest(classes = ApiServerApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureWireMock(port = 0)
@Import({FixedClockConfig.class})
public abstract class IntegrationTestSupport {

    @ServiceConnection
    @SuppressWarnings("resource")
    protected static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @ServiceConnection(name = "redis")
    @SuppressWarnings("resource")
    protected static final GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine").withExposedPorts(6379);

    static {
        postgres.start();
        redis.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> postgres.getJdbcUrl() + "?stringtype=unspecified");
    }

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @MockBean
    protected FcmInitializer fcmInitializer;

    @MockBean
    protected JpaAuditingConfig jpaAuditingConfig;

    @MockBean
    protected StorageWriter storageWriter;

    @MockBean
    protected KakaoAuthClient kakaoAuthClient;

    @MockBean
    protected KakaoUserInfoClient kakaoUserInfoClient;
}
