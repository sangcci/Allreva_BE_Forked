package com.backend.allreva.module.recruitment.chat.domain.message;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Content {

    private ContentType contentType;
    private String payload;

}
