package com.backend.allreva.module.member.application.dto;

import com.backend.allreva.module.member.domain.value.LoginProvider;

public record OAuthRegisterRequest(String email, LoginProvider loginProvider, String profileImageUrl) {}
