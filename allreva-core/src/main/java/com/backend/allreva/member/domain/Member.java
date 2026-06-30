package com.backend.allreva.member.domain;

import com.backend.allreva.common.model.Email;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Member {

    private Long id;
    private Email email;
    private MemberRole memberRole;
    private MemberStatus memberStatus;
    private LoginProvider loginProvider;
    private MemberInfo memberInfo;
    private RefundAccount refundAccount;

    @Builder
    private Member(
            final Long id,
            final Email email,
            final MemberRole memberRole,
            final MemberStatus memberStatus,
            final LoginProvider loginProvider,
            final MemberInfo memberInfo,
            final RefundAccount refundAccount,
            final String nickname,
            final String introduce,
            final String profileImageUrl) {
        this.id = id;
        this.email = email;
        this.memberRole = memberRole;
        this.memberStatus = memberStatus != null ? memberStatus : MemberStatus.ACTIVE;
        this.loginProvider = loginProvider;
        this.memberInfo = memberInfo != null
                ? memberInfo
                : MemberInfo.builder()
                        .nickname(nickname)
                        .introduce(introduce)
                        .profileImageUrl(profileImageUrl)
                        .build();
        this.refundAccount = refundAccount;
    }

    public void updateInfo(final String nickname, final String introduce, final String profileImageUrl) {
        this.memberInfo = MemberInfo.builder()
                .nickname(nickname)
                .introduce(introduce)
                .profileImageUrl(profileImageUrl)
                .build();
    }

    public void completeOnboarding() {
        if (memberStatus == MemberStatus.REGISTERED) {
            memberStatus = MemberStatus.ACTIVE;
        }
    }

    public void updateRefundAccount(final String bank, final String number) {
        this.refundAccount = RefundAccount.builder().bank(bank).number(number).build();
    }

    public void resetRefundAccount() {
        this.refundAccount = RefundAccount.builder().bank("").number("").build();
    }
}
