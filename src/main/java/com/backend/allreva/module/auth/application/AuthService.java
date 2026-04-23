package com.backend.allreva.module.auth.application;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.common.model.Email;
import com.backend.allreva.module.auth.application.dto.UserInfo;
import com.backend.allreva.module.auth.application.dto.UserInfoResponse;
import com.backend.allreva.module.auth.exception.JwtErrorCode;
import com.backend.allreva.module.member.application.MemberService;
import com.backend.allreva.module.member.application.dto.OAuthRegisterRequest;
import com.backend.allreva.module.member.domain.Member;
import com.backend.allreva.module.member.domain.MemberRepository;
import com.backend.allreva.module.member.domain.value.LoginProvider;
import com.backend.allreva.module.member.exception.MemberErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final OAuth2LoginService oAuth2LoginService;
    private final JwtService jwtService;
    private final MemberRepository memberRepository;
    private final MemberService memberService;

    /**
     * 카카오 로그인을 검증합니다.
     *
     * @param authorizationCode 인가 코드
     * @return 로그인 응답
     */
    public UserInfoResponse kakaoLogin(final String authorizationCode) {
        UserInfo userInfo = oAuth2LoginService.getUserInfo(authorizationCode);

        Email emailVO = Email.builder().email(userInfo.email()).build();
        LoginProvider loginProvider = userInfo.loginProvider();
        Member member = memberRepository
                .findByEmailAndLoginProvider(emailVO, loginProvider)
                .orElseGet(() -> memberService.registerByOAuth(new OAuthRegisterRequest(
                        userInfo.email(), userInfo.loginProvider(), userInfo.profileImageUrl())));

        return getMemberInfo(member);
    }

    private UserInfoResponse getMemberInfo(final Member member) {
        // token 생성
        Long memberId = member.getId();
        String accessToken = jwtService.generateAccessToken(String.valueOf(memberId));
        String refreshToken = jwtService.generateRefreshToken(String.valueOf(memberId));

        // redis에 RefreshToken 저장
        jwtService.updateRefreshToken(refreshToken, memberId);

        return UserInfoResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .email(member.getEmail().getEmail())
                .nickname(member.getMemberInfo().getNickname())
                .profileImageUrl(member.getMemberInfo().getProfileImageUrl())
                .build();
    }

    /**
     * Access Token을 재발급합니다.
     *
     * @param refreshToken Refresh Token @Return 재발급된 Access Token 및 Refresh Token
     */
    public UserInfoResponse reissueAccessToken(final String refreshToken) {
        // refresh token 검증
        if (refreshToken == null) {
            throw new CustomException(JwtErrorCode.TOKEN_EMPTY);
        }
        jwtService.validateToken(refreshToken);
        jwtService.validRefreshTokenExistInRedis(refreshToken);

        String memberId = jwtService.extractMemberId(refreshToken);

        // member db 확인
        Member member = memberRepository
                .findById(Long.valueOf(memberId))
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));

        // access token 재발급
        String generatedAccessToken = jwtService.generateAccessToken(memberId);

        // token rotate
        String generateRefreshToken = jwtService.generateRefreshToken(memberId);
        jwtService.updateRefreshToken(generateRefreshToken, Long.valueOf(memberId));

        return UserInfoResponse.builder()
                .accessToken(generatedAccessToken)
                .refreshToken(generateRefreshToken)
                .email(member.getEmail().getEmail())
                .nickname(member.getMemberInfo().getNickname())
                .profileImageUrl(member.getMemberInfo().getProfileImageUrl())
                .build();
    }

    /** Redis에 저장된 Refresh Token을 제거합니다. */
    public void logout(final String refreshToken) {
        if (refreshToken == null) {
            throw new CustomException(JwtErrorCode.TOKEN_EMPTY);
        }

        // redis refresh token은 따로 만료기간이 없어서 영구 저장됨 -> 없다면 문제 있는거.
        jwtService.validRefreshTokenExistInRedis(refreshToken);

        jwtService.deleteRefreshTokenInRedis(refreshToken);
    }
}
