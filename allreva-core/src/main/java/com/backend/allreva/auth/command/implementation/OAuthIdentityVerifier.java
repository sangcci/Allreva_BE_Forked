package com.backend.allreva.auth.command.implementation;

public interface OAuthIdentityVerifier {

    OAuthMember verify(String authorizationCode);
}
