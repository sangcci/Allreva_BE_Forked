package com.backend.allreva.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

@DataJpaTest(
        properties = {
            "spring.jpa.open-in-view=false",
            "spring.jpa.hibernate.ddl-auto=none",
            "spring.jpa.properties.hibernate.format_sql=true",
            "spring.jpa.properties.hibernate.show_sql=false",
            "spring.flyway.enabled=true",
            "spring.flyway.mixed=true",
            "spring.flyway.postgresql.transactional-lock=false",
            "spring.cloud.aws.s3.bucket=test-bucket",
            "spring.cloud.aws.credentials.access-key=test",
            "spring.cloud.aws.credentials.secret-key=test",
            "spring.cloud.aws.region.static=us-east-1"
        })
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class DataJpaTestSupport {

    @ServiceConnection
    @SuppressWarnings("resource")
    protected static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    static {
        postgres.start();
    }

    @DynamicPropertySource
    static void configureProperties(final DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> postgres.getJdbcUrl() + "?stringtype=unspecified");
    }

    @Autowired
    protected TestEntityManager entityManager;
}
