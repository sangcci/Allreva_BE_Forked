package com.backend.allreva.auth;

import com.backend.allreva.auth.command.implementation.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("!prod")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test")
public class TestAuthController implements TestAuthControllerSwagger {

    private final JwtTokenProvider jwtService;

    @Override
    @GetMapping("/token/{memberId}")
    public String getToken(@PathVariable final Long memberId) {
        return jwtService.generateAccessToken(memberId.toString());
    }
}
