package com.backend.allreva.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "[테스트] 인증 API", description = "테스트용 토큰 발급 API")
public interface TestAuthControllerSwagger {

    @Operation(summary = "테스트 토큰 발급", description = "memberId로 JWT access token을 즉시 발급합니다.")
    String getToken(Long memberId);
}
