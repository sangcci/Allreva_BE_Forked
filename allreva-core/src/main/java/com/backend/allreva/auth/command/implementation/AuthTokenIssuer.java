package com.backend.allreva.auth.command.implementation;

import com.backend.allreva.auth.command.output.AuthResult;
import com.backend.allreva.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthTokenIssuer {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenWriter refreshTokenWriter;

    public AuthResult issue(final Member member) {
        Long memberId = member.getId();
        String subject = String.valueOf(memberId);
        String accessToken = jwtTokenProvider.generateAccessToken(subject);
        String refreshToken = jwtTokenProvider.generateRefreshToken(subject);
        refreshTokenWriter.replace(refreshToken, memberId);

        return AuthResult.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .email(member.getEmail().getEmail())
                .nickname(member.getMemberInfo().getNickname())
                .profileImageUrl(member.getMemberInfo().getProfileImageUrl())
                .memberStatus(member.getMemberStatus())
                .build();
    }
}
