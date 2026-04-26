package com.backend.allreva.module.member.application.dto;

import com.backend.allreva.module.member.domain.Member;

public record MemberDetailResponse(
        String email, String nickname, String introduce, String profileImageUrl, String bank, String number) {

    public static MemberDetailResponse from(final Member member) {
        return new MemberDetailResponse(
                member.getEmail().getEmail(),
                member.getMemberInfo().getNickname(),
                member.getMemberInfo().getIntroduce(),
                member.getMemberInfo().getProfileImageUrl(),
                member.getRefundAccount().getBank(),
                member.getRefundAccount().getNumber());
    }
}
