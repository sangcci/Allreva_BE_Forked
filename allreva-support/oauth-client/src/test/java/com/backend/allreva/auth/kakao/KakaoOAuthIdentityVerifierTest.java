package com.backend.allreva.auth.kakao;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.backend.allreva.auth.command.implementation.OAuthMember;
import com.backend.allreva.member.domain.LoginProvider;
import com.backend.allreva.support.OAuthClientTestSupport;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@DisplayName("KakaoOAuthIdentityVerifier 테스트")
class KakaoOAuthIdentityVerifierTest extends OAuthClientTestSupport {

    @Autowired
    private KakaoOAuthIdentityVerifier verifier;

    @Test
    @DisplayName("인가 코드로 카카오 신원을 검증한다")
    void verify_oauth_member() {
        // given
        stubTokenResponse();
        stubUserInfoResponse("profile.jpg");

        // when
        OAuthMember oAuthMember = verifier.verify("auth-code");

        // then
        assertSoftly(softly -> {
            softly.assertThat(oAuthMember.loginProvider()).isEqualTo(LoginProvider.KAKAO);
            softly.assertThat(oAuthMember.providerId()).isEqualTo("kakao-id");
            softly.assertThat(oAuthMember.email()).isEqualTo("member@example.com");
            softly.assertThat(oAuthMember.nickname()).isEqualTo("닉네임");
            softly.assertThat(oAuthMember.profileImageUrl()).isEqualTo("profile.jpg");
        });
        verify(postRequestedFor(urlPathEqualTo("/oauth/token"))
                .withQueryParam("client_id", equalTo("client-id"))
                .withQueryParam("redirect_uri", equalTo("http://localhost/callback"))
                .withQueryParam("code", equalTo("auth-code"))
                .withQueryParam("grant_type", equalTo("authorization_code"))
                .withQueryParam("client_secret", equalTo("client-secret")));
        verify(WireMock.getRequestedFor(urlEqualTo("/v2/user/me"))
                .withHeader(HttpHeaders.AUTHORIZATION, equalTo("Bearer access-token")));
    }

    @Test
    @DisplayName("프로필 이미지가 없으면 빈 문자열로 변환한다")
    void verify_oauth_member_without_profile_image() {
        // given
        stubTokenResponse();
        stubUserInfoResponse(null);

        // when
        OAuthMember oAuthMember = verifier.verify("auth-code");

        // then
        assertSoftly(softly -> {
            softly.assertThat(oAuthMember.profileImageUrl()).isEmpty();
            softly.assertThat(oAuthMember.nickname()).isEqualTo("닉네임");
        });
    }

    private void stubTokenResponse() {
        WireMock.stubFor(post(urlPathEqualTo("/oauth/token"))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                {
                                  "access_token": "access-token",
                                  "refresh_token": "refresh-token",
                                  "token_type": "bearer",
                                  "expires_in": 3600,
                                  "refresh_token_expires_in": 7200
                                }
                                """)));
    }

    private void stubUserInfoResponse(final String profileImageUrl) {
        String profileImageField = profileImageUrl == null ? "null" : "\"" + profileImageUrl + "\"";
        WireMock.stubFor(get(urlEqualTo("/v2/user/me"))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                {
                                  "id": "kakao-id",
                                  "kakao_account": {
                                    "email": "member@example.com",
                                    "profile": {
                                      "nickname": "닉네임",
                                      "profile_image_url": %s
                                    }
                                  }
                                }
                                """.formatted(profileImageField))));
    }
}
