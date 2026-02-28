package com.backend.allreva.support;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.backend.allreva.common.config.FcmInitializer;
import com.backend.allreva.common.config.JpaAuditingConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@Import({AsyncAspect.class, FixedClockConfig.class})
public abstract class IntegrationTestSupport {

    static final PostgreSQLContainer<?> postgres;

    @SuppressWarnings("resource")
    static final GenericContainer<?> redis;

    static {
        postgres = new PostgreSQLContainer<>("postgres:16").withInitScript("db/trgm-indexes.sql");
        redis = new GenericContainer<>("redis:7-alpine").withExposedPorts(6379);
        postgres.start();
        redis.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> postgres.getJdbcUrl() + "?stringtype=unspecified");
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    @Autowired
    protected AsyncAspect asyncAspect;

    @MockBean
    protected FcmInitializer fcmInitializer;

    @MockBean
    protected JpaAuditingConfig jpaAuditingConfig;
}
