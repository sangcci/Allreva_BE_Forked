package com.backend.allreva.auth.kakao;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.backend.allreva.auth.command.implementation.OAuthMember;
import com.backend.allreva.member.domain.LoginProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("KakaoOAuthMemberMapper 단위 테스트")
class KakaoOAuthMemberMapperTest {

    private final KakaoOAuthMemberMapper mapper = new KakaoOAuthMemberMapper();

    @Nested
    @DisplayName("toOAuthMember 메서드는")
    class Describe_toOAuthMember {

        @Test
        void 카카오_회원_정보를_OAuthMember로_변환한다() {
            KakaoUserInfo kakaoUserInfo = new KakaoUserInfo(
                    "kakao-id",
                    new KakaoUserInfo.KakaoAccount(
                            "member@example.com", new KakaoUserInfo.KakaoAccount.Profile("닉네임", "profile.jpg")));

            OAuthMember result = mapper.toOAuthMember(kakaoUserInfo);

            assertSoftly(softly -> {
                softly.assertThat(result.loginProvider()).isEqualTo(LoginProvider.KAKAO);
                softly.assertThat(result.providerId()).isEqualTo("kakao-id");
                softly.assertThat(result.email()).isEqualTo("member@example.com");
                softly.assertThat(result.nickname()).isEqualTo("닉네임");
                softly.assertThat(result.profileImageUrl()).isEqualTo("profile.jpg");
            });
        }

        @Test
        void 프로필_이미지가_없으면_빈_문자열로_변환한다() {
            KakaoUserInfo kakaoUserInfo = new KakaoUserInfo(
                    "kakao-id",
                    new KakaoUserInfo.KakaoAccount(
                            "member@example.com", new KakaoUserInfo.KakaoAccount.Profile("닉네임", null)));

            OAuthMember result = mapper.toOAuthMember(kakaoUserInfo);

            assertSoftly(softly -> {
                softly.assertThat(result.profileImageUrl()).isEmpty();
                softly.assertThat(result.nickname()).isEqualTo("닉네임");
            });
        }
    }
}
