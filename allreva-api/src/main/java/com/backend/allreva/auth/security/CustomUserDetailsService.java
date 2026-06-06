package com.backend.allreva.auth.security;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.member.domain.Member;
import com.backend.allreva.member.domain.MemberErrorCode;
import com.backend.allreva.member.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(final String memberId) throws UsernameNotFoundException {
        Member member = memberRepository
                .findById(Long.valueOf(memberId))
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));

        return new PrincipalDetails(member);
    }
}
