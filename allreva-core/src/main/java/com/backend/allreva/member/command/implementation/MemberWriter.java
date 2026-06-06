package com.backend.allreva.member.command.implementation;

import com.backend.allreva.member.domain.Member;
import com.backend.allreva.member.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberWriter {

    private final MemberRepository memberRepository;

    public Member save(final Member member) {
        return memberRepository.save(member);
    }
}
