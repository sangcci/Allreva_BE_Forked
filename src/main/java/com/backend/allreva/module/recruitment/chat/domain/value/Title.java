package com.backend.allreva.module.recruitment.chat.domain.value;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Title {

    @Column(name = "title")
    private String value;

    public Title(final String value) {
        this.value = value;
    }
}
