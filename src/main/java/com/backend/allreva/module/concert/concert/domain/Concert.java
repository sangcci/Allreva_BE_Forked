package com.backend.allreva.module.concert.concert.domain;

import com.backend.allreva.common.model.BaseEntity;
import com.backend.allreva.common.model.Image;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = @Index(name = "idx_hall_code", columnList = "hall_code"))
@Entity
public class Concert extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long viewCount;

    @Embedded
    private Code code;

    @Embedded
    private ConcertInfo concertInfo;

    @ElementCollection
    @CollectionTable(
            name = "concert_episode",
            joinColumns = @JoinColumn(name = "id")
    )
    @Column(name = "episode")
    private List<String> episodes;


    @Embedded
    @AttributeOverride(name = "url", column = @Column(name = "poster"))
    private Image poster;

    @ElementCollection
    @CollectionTable(
            name = "concert_image",
            joinColumns = @JoinColumn(name = "id")
    )
    private List<Image> detailImages;


    @ElementCollection
    @CollectionTable(
            name = "concert_seller",
            joinColumns = @JoinColumn(name = "id")
    )
    private Set<Seller> sellers;


    public void updateFrom(
            final Code code,
            final ConcertInfo concertInfo,
            final List<String> episodes,
            final Image poster,
            final List<Image> detailImages,
            final Set<Seller> sellers
    ) {
        this.code = code;
        this.concertInfo = concertInfo;
        this.episodes = episodes;
        this.poster = poster;
        this.detailImages = detailImages;
        this.sellers = sellers;
    }


    @Builder
    private Concert(
            final Code code,
            final ConcertInfo concertInfo,
            final List<String> episodes,
            final Image poster,
            final List<Image> detailImages,
            final Set<Seller> sellers
    ) {
        this.code = code;
        this.concertInfo = concertInfo;
        this.episodes = episodes;
        this.poster = poster;
        this.detailImages = detailImages;
        this.sellers = sellers;
        this.viewCount = 0L;
    }

    public void addViewCount(final int count) {
        this.viewCount += count;
    }
}
