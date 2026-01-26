package com.backend.allreva.module.chat.application.dto;

import com.backend.allreva.common.model.Image;
import lombok.Getter;

@Getter
public class GroupChatOverviewResponse {

    private final String title;
    private final String description;
    private final int headcount;

    private final Image image;

    public GroupChatOverviewResponse(
            final String title,
            final String description,
            final int headcount,
            final Image image
    ) {
        this.title = title;
        this.description = description;
        this.headcount = headcount;
        this.image = image;
    }
}
