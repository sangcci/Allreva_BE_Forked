package com.backend.allreva.member.request;

import com.backend.allreva.common.model.Image;
import com.backend.allreva.member.domain.LoginProvider;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record MemberRegisterRequest(
        @NotBlank @Email String email,
        @NotBlank String nickname,
        String introduce,
        @NotNull LoginProvider loginProvider,
        @NotNull Image image) {}
