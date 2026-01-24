package com.backend.allreva.module.auth.application.dto;

import com.backend.allreva.module.member.domain.value.LoginProvider;
import lombok.Builder;

@Builder
public record UserInfo(
        LoginProvider loginProvider,
        String providerId,
        String nickname,
        String email,
        String profileImageUrl) {

}
