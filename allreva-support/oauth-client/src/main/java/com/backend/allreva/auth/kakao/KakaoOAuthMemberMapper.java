package com.backend.allreva.auth.kakao;

import com.backend.allreva.auth.command.implementation.OAuthMember;
import com.backend.allreva.member.domain.LoginProvider;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class KakaoOAuthMemberMapper {

    public OAuthMember toOAuthMember(final KakaoUserInfo kakaoUserInfo) {
        KakaoUserInfo.KakaoAccount kakaoAccount = kakaoUserInfo.kakaoAccount();
        KakaoUserInfo.KakaoAccount.Profile profile = kakaoAccount.profile();

        return OAuthMember.builder()
                .loginProvider(LoginProvider.KAKAO)
                .providerId(kakaoUserInfo.id())
                .email(kakaoAccount.email())
                .nickname(profile.nickname())
                .profileImageUrl(Objects.requireNonNullElse(profile.profileImageUrl(), ""))
                .build();
    }
}
