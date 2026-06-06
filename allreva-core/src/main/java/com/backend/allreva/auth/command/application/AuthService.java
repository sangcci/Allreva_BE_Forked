package com.backend.allreva.auth.command.application;

import com.backend.allreva.auth.command.implementation.AuthTokenIssuer;
import com.backend.allreva.auth.command.implementation.JwtTokenProvider;
import com.backend.allreva.auth.command.implementation.OAuthIdentityVerifier;
import com.backend.allreva.auth.command.implementation.OAuthMember;
import com.backend.allreva.auth.command.implementation.OAuthMemberLinker;
import com.backend.allreva.auth.command.implementation.RefreshTokenReader;
import com.backend.allreva.auth.command.implementation.RefreshTokenWriter;
import com.backend.allreva.auth.command.output.AuthResult;
import com.backend.allreva.member.command.implementation.MemberReader;
import com.backend.allreva.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final OAuthIdentityVerifier oAuthIdentityVerifier;
    private final OAuthMemberLinker oAuthMemberLinker;
    private final MemberReader memberReader;
    private final AuthTokenIssuer authTokenIssuer;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenReader refreshTokenReader;
    private final RefreshTokenWriter refreshTokenWriter;

    public AuthResult kakaoLogin(final String authorizationCode) {
        OAuthMember oAuthMember = oAuthIdentityVerifier.verify(authorizationCode);
        Member member = oAuthMemberLinker.link(oAuthMember);
        return authTokenIssuer.issue(member);
    }

    public AuthResult reissueAccessToken(final String refreshToken) {
        jwtTokenProvider.validateToken(refreshToken);
        refreshTokenReader.getByToken(refreshToken);

        Long memberId = jwtTokenProvider.extractMemberId(refreshToken);
        Member member = memberReader.getById(memberId);
        return authTokenIssuer.issue(member);
    }

    public void logout(final String refreshToken) {
        refreshTokenReader.getByToken(refreshToken);
        refreshTokenWriter.deleteByToken(refreshToken);
    }
}
