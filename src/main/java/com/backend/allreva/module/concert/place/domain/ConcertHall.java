package com.backend.allreva.module.concert.place.domain;

import com.backend.allreva.module.concert.place.domain.value.ConvenienceInfo;
import com.backend.allreva.module.concert.place.domain.value.Location;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ConcertHall {

    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    private int seatScale;

    @Embedded
    private ConvenienceInfo convenienceInfo;

    @Embedded
    private Location location;

    @Builder
    private ConcertHall(
            final String id,
            final String name,
            final int seatScale,
            final ConvenienceInfo convenienceInfo,
            final Location location) {
        this.id = id;
        this.name = name;
        this.seatScale = seatScale;
        this.convenienceInfo = convenienceInfo;
        this.location = location;
    }
}
