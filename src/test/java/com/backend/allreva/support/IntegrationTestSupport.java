package com.backend.allreva.support;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.backend.allreva.common.config.JpaAuditingConfig;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@MockBean(JpaAuditingConfig.class)
@AutoConfigureMockMvc(addFilters = false)
public abstract class IntegrationTestSupport {

    @Autowired
    protected AsyncAspect asyncAspect;
}
