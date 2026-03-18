package com.backend.allreva.module.auth.presentation;

import com.backend.allreva.module.auth.application.JwtService;
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

    private final JwtService jwtService;

    @GetMapping("/token/{memberId}")
    public String getToken(@PathVariable Long memberId) {
        return jwtService.generateAccessToken(memberId.toString());
    }
}
