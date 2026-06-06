package com.backend.allreva.member.command.input;

import com.backend.allreva.common.model.Image;
import lombok.Builder;

@Builder
public record MemberInfoUpdateCommand(String nickname, String introduce, Image image) {}
