package com.backend.allreva.member.query.implementation;

import com.backend.allreva.member.domain.NicknameDuplication;
import com.backend.allreva.member.query.model.MemberDetailResult;
import java.util.Optional;

public interface MemberFinderPort {

    Optional<MemberDetailResult> findById(Long id);

    NicknameDuplication findNicknameDuplication(String nickname);
}
