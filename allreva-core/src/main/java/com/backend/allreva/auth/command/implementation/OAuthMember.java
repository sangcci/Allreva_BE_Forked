package com.backend.allreva.auth.command.implementation;

import com.backend.allreva.member.domain.LoginProvider;
import lombok.Builder;

@Builder
public record OAuthMember(
        LoginProvider loginProvider, String providerId, String nickname, String email, String profileImageUrl) {}
