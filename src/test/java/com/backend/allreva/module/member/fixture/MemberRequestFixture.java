package com.backend.allreva.module.member.fixture;

import com.backend.allreva.common.model.Image;
import com.backend.allreva.module.member.application.dto.MemberArtistRequest;
import com.backend.allreva.module.member.application.dto.MemberRegisterRequest;
import com.backend.allreva.module.member.application.dto.RefundAccountRequest;
import com.backend.allreva.module.member.domain.value.LoginProvider;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MemberRequestFixture {

    public static MemberRegisterRequest createMemberRegisterRequestWithArtists(List<MemberArtistRequest> artists) {
        return MemberRegisterRequest.builder()
                .email("test@email.com")
                .nickname("testNickname")
                .loginProvider(LoginProvider.GOOGLE)
                .introduce("introduce")
                .memberArtistRequests(artists)
                .image(new Image("https://example.com/profile.jpg"))
                .build();
    }

    public static RefundAccountRequest createRefundAccountRequest() {
        return RefundAccountRequest.builder()
                .bank("국민은행")
                .number("123-456-789012")
                .build();
    }
}
