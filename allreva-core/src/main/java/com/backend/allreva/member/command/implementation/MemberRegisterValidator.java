package com.backend.allreva.member.command.implementation;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.common.model.Email;
import com.backend.allreva.member.domain.LoginProvider;
import com.backend.allreva.member.domain.MemberErrorCode;
import com.backend.allreva.member.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberRegisterValidator {

    private final MemberRepository memberRepository;

    public void validateNotRegistered(final String email, final LoginProvider loginProvider) {
        Email memberEmail = Email.builder().email(email).build();
        if (memberRepository
                .findByEmailAndLoginProvider(memberEmail, loginProvider)
                .isPresent()) {
            throw new CustomException(MemberErrorCode.DUPLICATE_MEMBER_ACCOUNT);
        }
    }
}
