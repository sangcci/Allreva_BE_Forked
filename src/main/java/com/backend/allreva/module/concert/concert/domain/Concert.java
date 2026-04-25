package com.backend.allreva.module.concert.concert.domain;

import com.backend.allreva.common.converter.ImageListConverter;
import com.backend.allreva.common.model.BaseEntity;
import com.backend.allreva.common.model.Image;
import com.backend.allreva.module.concert.concert.domain.value.ConcertInfo;
import com.backend.allreva.module.concert.concert.domain.value.Seller;
import com.backend.allreva.module.concert.concert.infra.jpa.SellerSetConverter;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = @Index(name = "idx_hall_code", columnList = "hall_code"))
@Entity
public class Concert extends BaseEntity {

    @Id
    private String concertCode;

    @Column(name = "hall_code", nullable = false)
    private String hallCode;

    @Embedded
    private ConcertInfo concertInfo;

    @Embedded
    @AttributeOverride(name = "url", column = @Column(name = "poster"))
    private Image poster;

    @Convert(converter = ImageListConverter.class)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "detail_images", columnDefinition = "jsonb", nullable = false)
    private List<Image> detailImages = new ArrayList<>();

    @Convert(converter = SellerSetConverter.class)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private Set<Seller> sellers = new HashSet<>();

    public void updateFrom(final Concert fetched) {
        this.hallCode = fetched.hallCode;
        this.concertInfo = fetched.concertInfo;
        this.poster = fetched.poster;
        this.detailImages = fetched.detailImages;
        this.sellers = fetched.sellers;
    }

    @Builder
    private Concert(
            final String concertCode,
            final String hallCode,
            final ConcertInfo concertInfo,
            final Image poster,
            final List<Image> detailImages,
            final Set<Seller> sellers) {
        this.concertCode = concertCode;
        this.hallCode = hallCode;
        this.concertInfo = concertInfo;
        this.poster = poster;
        this.detailImages = detailImages != null ? detailImages : this.detailImages;
        this.sellers = sellers != null ? sellers : this.sellers;
    }
}
