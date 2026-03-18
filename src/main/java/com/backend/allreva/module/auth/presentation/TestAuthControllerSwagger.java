package com.backend.allreva.module.auth.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "[테스트] 인증 API", description = "부하테스트용 토큰 발급 API (staging 전용, prod 비활성화)")
public interface TestAuthControllerSwagger {

    @Operation(
            summary = "테스트 토큰 발급",
            description = "memberId로 JWT access token을 즉시 발급합니다.\n"
                    + "부하테스트 시 각 VU에 서로 다른 토큰을 할당하기 위해 사용합니다.\n"
                    + "⚠️ prod 환경에서는 비활성화됩니다.")
    String getToken(Long memberId);
}
