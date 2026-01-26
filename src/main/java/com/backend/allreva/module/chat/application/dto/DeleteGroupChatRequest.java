package com.backend.allreva.module.chat.application.dto;

import jakarta.validation.constraints.NotBlank;

public record DeleteGroupChatRequest(

        @NotBlank
        Long groupChatId
) {
}
