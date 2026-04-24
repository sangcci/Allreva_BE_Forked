package com.backend.allreva.module.auth.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.common.model.Email;
import com.backend.allreva.module.auth.application.AuthService;
import com.backend.allreva.module.auth.application.JwtService;
import com.backend.allreva.module.auth.application.dto.UserInfoResponse;
import com.backend.allreva.module.auth.domain.RefreshTokenRepository;
import com.backend.allreva.module.auth.exception.JwtErrorCode;
import com.backend.allreva.module.auth.oauth2.KakaoToken;
import com.backend.allreva.module.auth.oauth2.KakaoUserInfo;
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

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("Auth 통합 테스트")
class AuthenticationIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private AuthService authService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private JwtService jwtService;

    @AfterEach
    void tearDown() {
        refreshTokenRepository.deleteAll();
        jdbcTemplate.execute("DELETE FROM member");
    }

    private void mockKakaoLogin(final String email) {
        given(kakaoAuthClient.getToken(anyString(), anyString(), anyString(), anyString(), anyString()))
                .willReturn(new KakaoToken("kakao-access-token", "kakao-refresh-token", "bearer", 3600, 5183999));
        given(kakaoUserInfoClient.getUserInfo(anyString()))
                .willReturn(new KakaoUserInfo(
                        "kakao-provider-id",
                        new KakaoUserInfo.KakaoAccount(
                                email,
                                new KakaoUserInfo.KakaoAccount.Profile("카카오닉네임", "https://example.com/profile.jpg"))));
    }

    @Nested
    @DisplayName("kakaoLogin 테스트")
    class Describe_kakaoLogin {

        @Nested
        @DisplayName("신규 회원이 카카오 로그인 시")
        class Context_신규_회원 {

            @BeforeEach
            void setUp() {
                mockKakaoLogin("newuser@kakao.com");
            }

            @Test
            void 멤버가_DB에_저장된다() {
                authService.kakaoLogin("auth-code");

                assertThat(memberRepository.findByEmailAndLoginProvider(
                                new Email("newuser@kakao.com"), LoginProvider.KAKAO))
                        .isPresent();
            }

            @Test
            void 닉네임이_user_로_시작하는_랜덤값으로_생성된다() {
                authService.kakaoLogin("auth-code");

                Member saved = memberRepository
                        .findByEmailAndLoginProvider(new Email("newuser@kakao.com"), LoginProvider.KAKAO)
                        .orElseThrow();
                assertThat(saved.getMemberInfo().getNickname()).startsWith("user-");
            }

            @Test
            void Kakao_닉네임이_저장되지_않는다() {
                authService.kakaoLogin("auth-code");

                assertThat(memberRepository.existsByMemberInfoNickname("카카오닉네임"))
                        .isFalse();
            }

            @Test
            void accessToken과_refreshToken이_반환된다() {
                UserInfoResponse response = authService.kakaoLogin("auth-code");

                assertThat(response.accessToken()).isNotBlank();
                assertThat(response.refreshToken()).isNotBlank();
            }

            @Test
            void refreshToken이_Redis에_저장된다() {
                UserInfoResponse response = authService.kakaoLogin("auth-code");

                assertThat(refreshTokenRepository.findRefreshTokenByToken(response.refreshToken()))
                        .isPresent();
            }
        }

        @Nested
        @DisplayName("기존 회원이 카카오 로그인 시")
        class Context_기존_회원 {

            @BeforeEach
            void setUp() {
                memberRepository.save(MemberFixture.createTestMember("existing@kakao.com", LoginProvider.KAKAO));
                mockKakaoLogin("existing@kakao.com");
            }

            @Test
            void 새_멤버가_생성되지_않는다() {
                authService.kakaoLogin("auth-code");

                assertThat(memberRepository.count()).isEqualTo(1);
            }

            @Test
            void 기존_멤버_이메일로_토큰이_반환된다() {
                UserInfoResponse response = authService.kakaoLogin("auth-code");

                assertThat(response.email()).isEqualTo("existing@kakao.com");
            }
        }

        @Nested
        @DisplayName("카카오가 프로필 이미지를 반환하지 않을 시")
        class Context_프로필_이미지_없음 {

            @BeforeEach
            void setUp() {
                given(kakaoAuthClient.getToken(anyString(), anyString(), anyString(), anyString(), anyString()))
                        .willReturn(
                                new KakaoToken("kakao-access-token", "kakao-refresh-token", "bearer", 3600, 5183999));
                given(kakaoUserInfoClient.getUserInfo(anyString()))
                        .willReturn(new KakaoUserInfo(
                                "kakao-provider-id",
                                new KakaoUserInfo.KakaoAccount(
                                        "noimage@kakao.com", new KakaoUserInfo.KakaoAccount.Profile("카카오닉네임", null))));
            }

            @Test
            void profileImageUrl이_빈문자열로_저장된다() {
                authService.kakaoLogin("auth-code");

                Member saved = memberRepository
                        .findByEmailAndLoginProvider(new Email("noimage@kakao.com"), LoginProvider.KAKAO)
                        .orElseThrow();
                assertThat(saved.getMemberInfo().getProfileImageUrl()).isEmpty();
            }
        }
    }

    @Nested
    @DisplayName("reissueAccessToken 테스트")
    class Describe_reissueAccessToken {

        @Nested
        @DisplayName("유효한 refresh token으로 요청 시")
        class Context_유효한_토큰 {

            private String refreshToken;
            private Long memberId;

            @BeforeEach
            void setUp() {
                Member member = memberRepository.save(MemberFixture.createTestMember());
                memberId = member.getId();
                refreshToken = jwtService.generateRefreshToken(String.valueOf(memberId));
                jwtService.updateRefreshToken(refreshToken, memberId);
            }

            @Test
            void 새_accessToken이_반환된다() {
                UserInfoResponse response = authService.reissueAccessToken(refreshToken);

                assertThat(response.accessToken()).isNotBlank();
            }

            @Test
            void 회원의_Redis_토큰이_재발급된_토큰으로_교체된다() {
                UserInfoResponse response = authService.reissueAccessToken(refreshToken);

                var storedToken = refreshTokenRepository.findRefreshTokenByMemberId(memberId);
                assertThat(storedToken).isPresent();
                assertThat(storedToken.get().getToken()).isEqualTo(response.refreshToken());
            }
        }

        @Nested
        @DisplayName("refresh token이 null일 때")
        class Context_null_토큰 {

            @Test
            void TOKEN_EMPTY_예외가_발생한다() {
                assertThatThrownBy(() -> authService.reissueAccessToken(null))
                        .isInstanceOf(CustomException.class)
                        .hasFieldOrPropertyWithValue("errorCode", JwtErrorCode.TOKEN_EMPTY);
            }
        }

        @Nested
        @DisplayName("Redis에 없는 refresh token으로 요청 시")
        class Context_Redis에_없는_토큰 {

            private String orphanToken;

            @BeforeEach
            void setUp() {
                Member member = memberRepository.save(MemberFixture.createTestMember());
                orphanToken = jwtService.generateRefreshToken(String.valueOf(member.getId()));
                // Redis에 저장하지 않음
            }

            @Test
            void TOKEN_NOT_FOUND_예외가_발생한다() {
                assertThatThrownBy(() -> authService.reissueAccessToken(orphanToken))
                        .isInstanceOf(CustomException.class)
                        .hasFieldOrPropertyWithValue("errorCode", JwtErrorCode.TOKEN_NOT_FOUND);
            }
        }
    }

    @Nested
    @DisplayName("logout 테스트")
    class Describe_logout {

        @Nested
        @DisplayName("로그인된 유저가 로그아웃 시")
        class Context_로그인_상태 {

            private String refreshToken;

            @BeforeEach
            void setUp() {
                Member member = memberRepository.save(MemberFixture.createTestMember());
                refreshToken = jwtService.generateRefreshToken(String.valueOf(member.getId()));
                jwtService.updateRefreshToken(refreshToken, member.getId());
            }

            @Test
            void Redis에서_refreshToken이_삭제된다() {
                authService.logout(refreshToken);

                assertThat(refreshTokenRepository.findRefreshTokenByToken(refreshToken))
                        .isEmpty();
            }
        }

        @Nested
        @DisplayName("refresh token이 null일 때")
        class Context_null_토큰 {

            @Test
            void TOKEN_EMPTY_예외가_발생한다() {
                assertThatThrownBy(() -> authService.logout(null))
                        .isInstanceOf(CustomException.class)
                        .hasFieldOrPropertyWithValue("errorCode", JwtErrorCode.TOKEN_EMPTY);
            }
        }
    }
}
