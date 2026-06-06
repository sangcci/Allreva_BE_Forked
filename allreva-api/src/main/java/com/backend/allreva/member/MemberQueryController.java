package com.backend.allreva.member;

import com.backend.allreva.auth.security.AuthMember;
import com.backend.allreva.common.web.response.View;
import com.backend.allreva.member.domain.Member;
import com.backend.allreva.member.query.application.MemberFinder;
import com.backend.allreva.member.query.model.MemberDetailResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/members")
public class MemberQueryController implements MemberQueryControllerSwagger {

    private final MemberFinder memberQueryService;

    @Override
    @GetMapping
    public View<MemberDetailResult> getMemberDetail(@AuthMember final Member member) {
        return View.onSuccess(memberQueryService.getById(member.getId()));
    }

    @Override
    @GetMapping("/check-nickname")
    public View<Boolean> isDuplicatedNickname(@RequestParam final String nickname) {
        return View.onSuccess(memberQueryService.isDuplicatedNickname(nickname).isDuplicated());
    }
}
