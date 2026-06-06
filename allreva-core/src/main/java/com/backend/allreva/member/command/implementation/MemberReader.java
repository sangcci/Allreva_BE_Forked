package com.backend.allreva.member.command.implementation;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.member.domain.Member;
import com.backend.allreva.member.domain.MemberErrorCode;
import com.backend.allreva.member.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberReader {

    private final MemberRepository memberRepository;

    public Member getById(final Long memberId) {
        return memberRepository
                .findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));
    }
}
