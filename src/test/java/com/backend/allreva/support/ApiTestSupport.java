package com.backend.allreva.support;

import com.backend.allreva.common.config.SecurityConfig;
import com.backend.allreva.module.member.domain.Member;
import com.backend.allreva.module.member.domain.value.MemberRole;
import com.backend.allreva.module.member.fixture.MemberFixture;
import com.backend.allreva.module.auth.presentation.AuthController;
import com.backend.allreva.module.auth.security.JwtAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = {
        AuthController.class }, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                JwtAuthenticationFilter.class, SecurityConfig.class }))
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public abstract class ApiTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    // @MockBean
    // protected MemberCommandFacade memberCommandFacade;

    protected Member member;

    @BeforeEach
    void setUp() {
        member = MemberFixture.createMember(1L, MemberRole.USER);
    }

    @AfterEach
    void cleanContextHolder() {
        SecurityContextHolder.clearContext();
    }
}
