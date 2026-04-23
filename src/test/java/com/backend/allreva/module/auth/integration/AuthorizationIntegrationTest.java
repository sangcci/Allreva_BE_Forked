package com.backend.allreva.module.auth.integration;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backend.allreva.module.auth.application.JwtService;
import com.backend.allreva.module.member.domain.Member;
import com.backend.allreva.module.member.domain.MemberRepository;
import com.backend.allreva.module.member.domain.value.LoginProvider;
import com.backend.allreva.module.member.fixture.MemberFixture;
import com.backend.allreva.support.IntegrationTestSupport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("Authorization 통합 테스트")
class AuthorizationIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private MemberRepository memberRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUpSecurity() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @AfterEach
    void tearDown() {
        memberRepository.deleteAllInBatch();
    }

    @Nested
    @DisplayName("인증 없이 접근할 때")
    class Describe_인증_없이 {

        @Nested
        @DisplayName("USER_LIST 경로에 요청 시")
        class Context_USER_LIST {

            @Test
            void HTTP_401이_반환된다() throws Exception {
                mockMvc.perform(get("/api/v1/members/check-nickname").param("nickname", "test"))
                        .andExpect(status().isUnauthorized());
            }
        }
    }

    @Nested
    @DisplayName("USER 권한으로 접근할 때")
    class Describe_USER_권한 {

        private String accessToken;

        @BeforeEach
        void setUp() {
            Member member =
                    memberRepository.save(MemberFixture.createTestMember("user@test.com", LoginProvider.GOOGLE));
            accessToken = jwtService.generateAccessToken(String.valueOf(member.getId()));
        }

        @Nested
        @DisplayName("USER_LIST 경로에 요청 시")
        class Context_USER_LIST {

            @Test
            void 정상_응답이_반환된다() throws Exception {
                mockMvc.perform(get("/api/v1/members/check-nickname")
                                .param("nickname", "test")
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                        .andExpect(status().isOk());
            }
        }

        @Nested
        @DisplayName("ADMIN_LIST 경로에 요청 시")
        class Context_ADMIN_LIST {

            @Test
            void HTTP_403이_반환된다() throws Exception {
                mockMvc.perform(get("/api/v1/admin/members").header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                        .andExpect(status().isForbidden());
            }
        }
    }
}
