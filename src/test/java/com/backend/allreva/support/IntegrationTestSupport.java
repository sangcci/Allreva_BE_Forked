package com.backend.allreva.support;

import com.backend.allreva.common.config.FcmInitializer;
import com.backend.allreva.common.config.JpaAuditingConfig;
import com.backend.allreva.common.storage.upload.StorageUploadService;
import com.backend.allreva.module.auth.oauth2.KakaoAuthClient;
import com.backend.allreva.module.auth.oauth2.KakaoUserInfoClient;
import com.backend.allreva.module.concert.artist.infra.spotify.SpotifyAccountClient;
import com.backend.allreva.module.concert.artist.infra.spotify.SpotifyClient;
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

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureWireMock(port = 0)
@Import({AsyncAspect.class, FixedClockConfig.class})
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
    protected AsyncAspect asyncAspect;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @MockBean
    protected FcmInitializer fcmInitializer;

    @MockBean
    protected JpaAuditingConfig jpaAuditingConfig;

    @MockBean
    protected StorageUploadService storageUploadService;

    @MockBean
    protected SpotifyClient spotifyClient;

    @MockBean
    protected SpotifyAccountClient spotifyAccountClient;

    @MockBean
    protected KakaoAuthClient kakaoAuthClient;

    @MockBean
    protected KakaoUserInfoClient kakaoUserInfoClient;
}
