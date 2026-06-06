package com.backend.allreva.member.query.model;

import com.backend.allreva.member.domain.Member;

public record MemberDetailResult(
        String email, String nickname, String introduce, String profileImageUrl, String bank, String number) {

    public static MemberDetailResult from(final Member member) {
        return new MemberDetailResult(
                member.getEmail().getEmail(),
                member.getMemberInfo().getNickname(),
                member.getMemberInfo().getIntroduce(),
                member.getMemberInfo().getProfileImageUrl(),
                member.getRefundAccount().getBank(),
                member.getRefundAccount().getNumber());
    }
}
