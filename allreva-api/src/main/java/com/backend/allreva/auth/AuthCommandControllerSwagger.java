package com.backend.allreva.auth;

import com.backend.allreva.common.web.response.View;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;

@Tag(name = "인증 API", description = "인증 Command API")
public interface AuthCommandControllerSwagger {

    @Operation(summary = "카카오 로그인", description = "인가코드로 카카오 OAuth2 로그인")
    View<AuthUserResponse> authKakaoLogin(
            String authorizationCode, HttpServletRequest request, HttpServletResponse response);

    @Operation(summary = "토큰 재발급", description = "refresh token으로 access token 재발급")
    View<Void> reissueToken(@NotBlank String refreshToken, HttpServletRequest request, HttpServletResponse response);

    @Operation(summary = "로그인 체크", description = "refresh token으로 로그인 상태 확인 및 토큰 재발급")
    View<AuthUserResponse> loginCheck(
            @NotBlank String refreshToken, HttpServletRequest request, HttpServletResponse response);

    @Operation(summary = "로그아웃", description = "refresh token 삭제")
    View<Void> logout(@NotBlank String refreshToken, HttpServletRequest request, HttpServletResponse response);
}
