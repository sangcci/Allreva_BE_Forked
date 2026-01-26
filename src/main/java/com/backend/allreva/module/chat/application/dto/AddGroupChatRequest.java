package com.backend.allreva.module.chat.application.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record AddGroupChatRequest(

        @Size(min = 1, max = 20)
        String title,

        @Max(50)
        @Min(2)
        int capacity
) {
}
