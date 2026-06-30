package com.backend.allreva.auth;

import com.backend.allreva.auth.command.output.AuthResult;
import com.backend.allreva.member.domain.MemberStatus;

public record AuthUserResponse(String email, String nickname, String profileImageUrl, MemberStatus memberStatus) {

    public static AuthUserResponse from(final AuthResult result) {
        return new AuthUserResponse(result.email(), result.nickname(), result.profileImageUrl(), result.memberStatus());
    }
}
