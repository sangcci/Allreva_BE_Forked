package com.backend.allreva.auth.command.output;

import lombok.Builder;

@Builder
public record AuthResult(
        String email, String nickname, String profileImageUrl, String accessToken, String refreshToken) {}
