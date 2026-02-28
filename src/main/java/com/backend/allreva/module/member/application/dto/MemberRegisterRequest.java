package com.backend.allreva.module.member.application.dto;

import com.backend.allreva.common.model.Email;
import com.backend.allreva.common.model.Image;
import com.backend.allreva.module.member.domain.Member;
import com.backend.allreva.module.member.domain.value.LoginProvider;
import com.backend.allreva.module.member.domain.value.MemberRole;
import java.util.List;
import lombok.Builder;

@Builder
public record MemberRegisterRequest(
        String email,
        String nickname,
        String introduce,
        LoginProvider loginProvider,
        List<MemberArtistRequest> memberArtistRequests,
        Image image) {

    public Member toEntity() {
        return Member.builder()
                .email(Email.builder().email(email).build())
                .nickname(nickname)
                .memberRole(MemberRole.USER)
                .introduce(introduce)
                .profileImageUrl(image.getUrl())
                .loginProvider(loginProvider)
                .build();
    }
}
