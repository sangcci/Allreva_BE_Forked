package com.backend.allreva.member.command.implementation;

import com.backend.allreva.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberInfoUpdater {

    public void update(
            final Member member, final String nickname, final String introduce, final String profileImageUrl) {
        member.updateInfo(nickname, introduce, profileImageUrl);
        member.completeOnboarding();
    }
}
