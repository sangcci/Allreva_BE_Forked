package com.backend.allreva.member.command.implementation;

import com.backend.allreva.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefundAccountUpdater {

    public void update(final Member member, final String bank, final String number) {
        member.updateRefundAccount(bank, number);
    }

    public void reset(final Member member) {
        member.resetRefundAccount();
    }
}
