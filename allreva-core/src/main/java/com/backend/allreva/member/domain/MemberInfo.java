package com.backend.allreva.member.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberInfo {

    private String nickname;
    private String introduce;
    private String profileImageUrl;

    @Builder
    private MemberInfo(final String nickname, final String introduce, final String profileImageUrl) {
        this.nickname = nickname;
        this.introduce = introduce;
        this.profileImageUrl = profileImageUrl;
    }
}
