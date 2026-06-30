package com.backend.allreva.auth.command.output;

import com.backend.allreva.member.domain.MemberStatus;
import lombok.Builder;

@Builder
public record AuthResult(
        String email,
        String nickname,
        String profileImageUrl,
        MemberStatus memberStatus,
        String accessToken,
        String refreshToken) {}
