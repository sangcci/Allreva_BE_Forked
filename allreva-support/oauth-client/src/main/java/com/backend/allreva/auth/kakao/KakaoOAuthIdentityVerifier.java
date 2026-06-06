package com.backend.allreva.auth.kakao;

import com.backend.allreva.auth.command.implementation.OAuthIdentityVerifier;
import com.backend.allreva.auth.command.implementation.OAuthMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KakaoOAuthIdentityVerifier implements OAuthIdentityVerifier {

    private static final String GRANT_TYPE_AUTHORIZATION_CODE = "authorization_code";
    private static final String BEARER_PREFIX = "Bearer ";

    private final KakaoAuthClient kakaoAuthClient;
    private final KakaoUserInfoClient kakaoUserInfoClient;
    private final KakaoOAuthProperties kakaoOAuthProperties;
    private final KakaoOAuthMemberMapper kakaoOAuthMemberMapper;

    @Override
    public OAuthMember verify(final String authorizationCode) {
        KakaoToken token = kakaoAuthClient.getToken(
                kakaoOAuthProperties.clientId(),
                kakaoOAuthProperties.redirectUri(),
                authorizationCode,
                GRANT_TYPE_AUTHORIZATION_CODE,
                kakaoOAuthProperties.clientSecret());

        KakaoUserInfo kakaoUserInfo = kakaoUserInfoClient.getUserInfo(BEARER_PREFIX + token.accessToken());
        return kakaoOAuthMemberMapper.toOAuthMember(kakaoUserInfo);
    }
}
