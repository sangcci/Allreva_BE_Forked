package com.backend.allreva.module.member.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MemberConstraints {

    public static final String UQ_EMAIL_PROVIDER = "uq_member_email_provider";
}
