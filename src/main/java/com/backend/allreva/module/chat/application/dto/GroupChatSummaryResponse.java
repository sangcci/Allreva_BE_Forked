package com.backend.allreva.module.chat.application.dto;

import com.backend.allreva.module.chat.domain.value.Title;
import com.backend.allreva.common.model.Image;

public record GroupChatSummaryResponse(

        Long id,

        Title title,
        Image Thumbnail,
        int headcount
) {
}
