package com.backend.allreva.member;

import com.backend.allreva.common.web.response.View;
import com.backend.allreva.member.domain.Member;
import com.backend.allreva.member.query.model.MemberDetailResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "회원 API", description = "회원 Query API")
public interface MemberQueryControllerSwagger {

    @SecurityRequirement(name = "USER")
    @Operation(summary = "회원 정보 조회", description = "**[회원]**")
    View<MemberDetailResult> getMemberDetail(Member member);

    @Operation(summary = "닉네임 중복 확인")
    View<Boolean> isDuplicatedNickname(String nickname);
}
