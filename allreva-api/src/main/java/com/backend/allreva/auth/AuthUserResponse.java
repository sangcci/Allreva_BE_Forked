package com.backend.allreva.auth;

import com.backend.allreva.auth.command.output.AuthResult;

public record AuthUserResponse(String email, String nickname, String profileImageUrl) {

    public static AuthUserResponse from(final AuthResult result) {
        return new AuthUserResponse(result.email(), result.nickname(), result.profileImageUrl());
    }
}
