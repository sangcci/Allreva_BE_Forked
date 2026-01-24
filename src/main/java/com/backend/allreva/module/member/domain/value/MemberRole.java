package com.backend.allreva.module.member.domain.value;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberRole {

    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN"),
    DEVELOPER("ROLE_DEVELOPER");

    private final String text;
}
