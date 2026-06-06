package com.backend.allreva.auth;

import com.backend.allreva.auth.command.application.AuthService;
import com.backend.allreva.auth.command.output.AuthResult;
import com.backend.allreva.common.web.response.View;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
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

    @Override
    @GetMapping("/token/kakao")
    public View<AuthUserResponse> authKakaoLogin(
            @RequestParam("code") final String authorizationCode,
            final HttpServletRequest request,
            final HttpServletResponse response) {
        AuthResult authResult = authCommandService.kakaoLogin(authorizationCode);
        applyAuthTokens(request, response, authResult);
        return View.onSuccess(AuthUserResponse.from(authResult));
    }

    @Override
    @GetMapping("/token/reissue")
    public View<Void> reissueToken(
            @NotBlank @CookieValue(name = "refreshToken", required = false) final String refreshToken,
            final HttpServletRequest request,
            final HttpServletResponse response) {
        AuthResult authResult = authCommandService.reissueAccessToken(refreshToken);
        applyAuthTokens(request, response, authResult);
        return View.onSuccess();
    }

    @Override
    @GetMapping("/login/check")
    public View<AuthUserResponse> loginCheck(
            @NotBlank @CookieValue(name = "refreshToken", required = false) final String refreshToken,
            final HttpServletRequest request,
            final HttpServletResponse response) {
        AuthResult authResult = authCommandService.reissueAccessToken(refreshToken);
        applyAuthTokens(request, response, authResult);
        return View.onSuccess(AuthUserResponse.from(authResult));
    }

    @Override
    @GetMapping("/logout")
    public View<Void> logout(
            @NotBlank @CookieValue(name = "refreshToken", required = false) final String refreshToken,
            final HttpServletRequest request,
            final HttpServletResponse response) {
        String domainName = extractDomainName(request);
        authCommandService.logout(refreshToken);
        refreshTokenCookieWriter.delete(response, domainName);
        return View.onSuccess();
    }

    private void applyAuthTokens(
            final HttpServletRequest request, final HttpServletResponse response, final AuthResult authResult) {
        String domainName = extractDomainName(request);
        refreshTokenCookieWriter.write(response, authResult.refreshToken(), domainName);
        response.addHeader("Authorization", "Bearer " + authResult.accessToken());
    }

    private static String extractDomainName(final HttpServletRequest request) {
        String origin = request.getHeader("Origin");
        if (origin != null) {
            return origin;
        }
        String referer = request.getHeader("Referer");
        if (referer != null) {
            return referer;
        }
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
    }
}
