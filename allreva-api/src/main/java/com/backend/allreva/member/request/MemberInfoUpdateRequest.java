package com.backend.allreva.member.request;

import com.backend.allreva.common.model.Image;
import com.backend.allreva.member.command.input.MemberInfoUpdateCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record MemberInfoUpdateRequest(
        @NotBlank String nickname,
        String introduce,
        @NotNull Image image) {

    public MemberInfoUpdateCommand toCommand() {
        return new MemberInfoUpdateCommand(nickname, introduce, image);
    }
}
