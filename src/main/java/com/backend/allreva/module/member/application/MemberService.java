package com.backend.allreva.module.member.application;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.common.model.Email;
import com.backend.allreva.module.member.application.dto.MemberDetailResponse;
import com.backend.allreva.module.member.application.dto.MemberRegisterRequest;
import com.backend.allreva.module.member.application.dto.NicknameDuplication;
import com.backend.allreva.module.member.application.dto.OAuthRegisterRequest;
import com.backend.allreva.module.member.application.dto.RefundAccountRequest;
import com.backend.allreva.module.member.domain.Member;
import com.backend.allreva.module.member.domain.MemberConstraints;
import com.backend.allreva.module.member.domain.MemberRepository;
import com.backend.allreva.module.member.domain.value.MemberRole;
import com.backend.allreva.module.member.exception.MemberErrorCode;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public MemberDetailResponse getById(final Long id) {
        Member member =
                memberRepository.findById(id).orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));
        return MemberDetailResponse.from(member);
    }

    @Transactional(readOnly = true)
    public NicknameDuplication isDuplicatedNickname(final String nickname) {
        boolean exists = memberRepository.existsByMemberInfoNickname(nickname);
        return new NicknameDuplication(exists);
    }

    @Transactional
    public Member registerByOAuth(final OAuthRegisterRequest request) {
        try {
            Member member = Member.builder()
                    .email(Email.builder().email(request.email()).build())
                    .loginProvider(request.loginProvider())
                    .memberRole(MemberRole.USER)
                    .nickname(generateUniqueNickname())
                    .introduce("")
                    .profileImageUrl(request.profileImageUrl())
                    .build();
            member.setDefaultRefundAccount();
            return memberRepository.saveAndFlush(member);
        } catch (DataIntegrityViolationException e) {
            if (e.getCause() instanceof ConstraintViolationException cve
                    && MemberConstraints.UQ_EMAIL_PROVIDER.equals(cve.getConstraintName())) {
                throw new CustomException(MemberErrorCode.DUPLICATE_OAUTH_MEMBER);
            }
            throw e;
        }
    }

    private String generateUniqueNickname() {
        return "user-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    @Transactional
    public void registerMember(final MemberRegisterRequest memberRegisterRequest) {
        Member member = memberRegisterRequest.toEntity();
        memberRepository.save(member);
    }

    @Transactional
    public void updateMemberInfo(final MemberRegisterRequest memberRegisterRequest, final Member member) {
        member.setMemberInfo(
                memberRegisterRequest.nickname(),
                memberRegisterRequest.introduce(),
                memberRegisterRequest.image().getUrl());
        memberRepository.save(member);
    }

    @Transactional
    public void registerRefundAccount(final RefundAccountRequest refundAccountRequest, final Member member) {
        member.setRefundAccount(refundAccountRequest.bank(), refundAccountRequest.number());
        memberRepository.save(member);
    }

    @Transactional
    public void deleteRefundAccount(final Member member) {
        member.setDefaultRefundAccount();
        memberRepository.save(member);
    }
}
