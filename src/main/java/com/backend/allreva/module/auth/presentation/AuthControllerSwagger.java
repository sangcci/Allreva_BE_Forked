package com.backend.allreva.module.auth.presentation;

import com.backend.allreva.common.web.response.Response;
import com.backend.allreva.module.auth.application.dto.UserInfoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Tag(name = "인증 API", description = "인증 관련 API")
public interface AuthControllerSwagger {

    @Operation(summary = "카카오 로그인", description = "인가코드로 카카오 OAuth2 로그인")
    Response<UserInfoResponse> authKakaoLogin(
            String authorizationCode, HttpServletRequest request, HttpServletResponse response);

    @Operation(summary = "토큰 재발급", description = "refresh token으로 access token 재발급")
    Response<Void> reissueToken(String refreshToken, HttpServletRequest request, HttpServletResponse response);

    @Operation(summary = "로그인 체크", description = "refresh token으로 로그인 상태 확인 및 토큰 재발급")
    Response<UserInfoResponse> loginCheck(
            String refreshToken, HttpServletRequest request, HttpServletResponse response);

    @Operation(summary = "로그아웃", description = "refresh token 삭제")
    Response<Void> logout(
            final String refreshToken, final HttpServletRequest request, final HttpServletResponse response);
}
