package com.backend.allreva.member.query.application;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.member.domain.MemberErrorCode;
import com.backend.allreva.member.domain.NicknameDuplication;
import com.backend.allreva.member.query.implementation.MemberFinderPort;
import com.backend.allreva.member.query.model.MemberDetailResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberFinder {

    private final MemberFinderPort memberFinderPort;

    @Transactional(readOnly = true)
    public MemberDetailResult getById(final Long id) {
        return memberFinderPort.findById(id).orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public NicknameDuplication isDuplicatedNickname(final String nickname) {
        return memberFinderPort.findNicknameDuplication(nickname);
    }
}
