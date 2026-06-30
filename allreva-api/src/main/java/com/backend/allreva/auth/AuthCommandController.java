package com.backend.allreva.auth;

import com.backend.allreva.auth.command.application.AuthService;
import com.backend.allreva.auth.command.output.AuthResult;
import com.backend.allreva.common.web.response.View;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthCommandController implements AuthCommandControllerSwagger {

    private final AuthService authCommandService;
    private final RefreshTokenCookieWriter refreshTokenCookieWriter;

    @Value("${auth.jwt.access-token.header}")
    private String accessTokenHeader;

    @Override
    @GetMapping("/token/kakao")
    public View<AuthUserResponse> authKakaoLogin(
            @RequestParam("code") final String authorizationCode,
            final HttpServletRequest request,
            final HttpServletResponse response) {
        AuthResult authResult = authCommandService.kakaoLogin(authorizationCode);
        applyAuthTokens(response, authResult);
        return View.onSuccess(AuthUserResponse.from(authResult));
    }

    @Override
    @GetMapping("/token/reissue")
    public View<Void> reissueToken(
            @NotBlank @CookieValue(name = "${auth.jwt.refresh-token.name}", required = false)
                    final String refreshTokenJwt,
            final HttpServletRequest request,
            final HttpServletResponse response) {
        AuthResult authResult = authCommandService.reissueAccessToken(refreshTokenJwt);
        applyAuthTokens(response, authResult);
        return View.onSuccess();
    }

    @Override
    @GetMapping("/login/check")
    public View<AuthUserResponse> loginCheck(
            @NotBlank @CookieValue(name = "${auth.jwt.refresh-token.name}", required = false)
                    final String refreshTokenJwt,
            final HttpServletRequest request,
            final HttpServletResponse response) {
        AuthResult authResult = authCommandService.reissueAccessToken(refreshTokenJwt);
        applyAuthTokens(response, authResult);
        return View.onSuccess(AuthUserResponse.from(authResult));
    }

    @Override
    @GetMapping("/logout")
    public View<Void> logout(
            @NotBlank @CookieValue(name = "${auth.jwt.refresh-token.name}", required = false)
                    final String refreshTokenJwt,
            final HttpServletRequest request,
            final HttpServletResponse response) {
        authCommandService.logout(refreshTokenJwt);
        refreshTokenCookieWriter.delete(response);
        return View.onSuccess();
    }

    private void applyAuthTokens(final HttpServletResponse response, final AuthResult authResult) {
        refreshTokenCookieWriter.write(response, authResult.refreshToken());
        response.addHeader(accessTokenHeader, "Bearer " + authResult.accessToken());
    }
}
