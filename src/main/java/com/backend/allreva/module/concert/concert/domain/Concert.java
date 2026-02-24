package com.backend.allreva.module.concert.concert.domain;

import com.backend.allreva.common.model.BaseEntity;
import com.backend.allreva.common.model.Image;
import com.backend.allreva.module.concert.concert.domain.value.Code;
import com.backend.allreva.module.concert.concert.domain.value.ConcertInfo;
import com.backend.allreva.module.concert.concert.domain.value.Seller;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.HashSet;
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

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private List<String> episodes = new ArrayList<>();


    @Embedded
    @AttributeOverride(name = "url", column = @Column(name = "poster"))
    private Image poster;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "detail_images", columnDefinition = "jsonb", nullable = false)
    private List<Image> detailImages = new ArrayList<>();


    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private Set<Seller> sellers = new HashSet<>();


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
