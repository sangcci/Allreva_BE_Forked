package com.backend.allreva.concert.concert;

import com.backend.allreva.common.converter.ImageVOListConverter;
import com.backend.allreva.common.model.Image;
import com.backend.allreva.common.persistence.BaseEntity;
import com.backend.allreva.common.persistence.ImageVO;
import com.backend.allreva.concert.concert.domain.Concert;
import com.backend.allreva.concert.concert.domain.ConcertInfo;
import com.backend.allreva.concert.concert.domain.ConcertStatus;
import com.backend.allreva.concert.concert.domain.DateInfo;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = @Index(name = "idx_hall_code", columnList = "hall_code"))
@Entity(name = "Concert")
public class ConcertEntity extends BaseEntity {

    @Id
    private String concertCode;

    @Column(name = "hall_code", nullable = false)
    private String hallCode;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String price;

    @Column(nullable = false, name = "prfstate")
    @Enumerated(EnumType.STRING)
    private ConcertStatus performStatus;

    private String host;

    @Column(nullable = false, name = "stdate")
    private LocalDate startDate;

    @Column(nullable = false, name = "eddate")
    private LocalDate endDate;

    @Column(nullable = false)
    private String timeTable;

    @Column(name = "poster")
    private String posterUrl;

    @Convert(converter = ImageVOListConverter.class)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "detail_images", columnDefinition = "jsonb", nullable = false)
    private List<ImageVO> detailImages = new ArrayList<>();

    @Convert(converter = SellerVOSetConverter.class)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private Set<SellerVO> sellers = new HashSet<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "cast_names", columnDefinition = "jsonb", nullable = false)
    private List<String> castNames = new ArrayList<>();

    private ConcertEntity(
            final String concertCode,
            final String hallCode,
            final ConcertInfo concertInfo,
            final Image poster,
            final List<ImageVO> detailImages,
            final Set<SellerVO> sellers,
            final List<String> castNames) {
        this.concertCode = concertCode;
        this.hallCode = hallCode;
        if (concertInfo != null) {
            this.title = concertInfo.getTitle();
            this.price = concertInfo.getPrice();
            this.performStatus = concertInfo.getPerformStatus();
            this.host = concertInfo.getHost();
            DateInfo dateInfo = concertInfo.getDateInfo();
            if (dateInfo != null) {
                this.startDate = dateInfo.getStartDate();
                this.endDate = dateInfo.getEndDate();
                this.timeTable = dateInfo.getTimeTable();
            }
        }
        this.posterUrl = poster != null ? poster.getUrl() : null;
        this.detailImages = detailImages != null ? detailImages : this.detailImages;
        this.sellers = sellers != null ? sellers : this.sellers;
        this.castNames = castNames != null ? castNames : this.castNames;
    }

    public static ConcertEntity from(final Concert concert) {
        return new ConcertEntity(
                concert.getConcertCode(),
                concert.getHallCode(),
                concert.getConcertInfo(),
                concert.getPoster(),
                concert.getDetailImages().stream().map(ImageVO::from).toList(),
                concert.getSellers().stream().map(SellerVO::from).collect(java.util.stream.Collectors.toSet()),
                concert.getCastNames());
    }

    public Concert toDomain() {
        return Concert.builder()
                .concertCode(concertCode)
                .hallCode(hallCode)
                .concertInfo(ConcertInfo.builder()
                        .title(title)
                        .price(price)
                        .performStatus(performStatus)
                        .host(host)
                        .dateInfo(DateInfo.builder()
                                .startDate(startDate)
                                .endDate(endDate)
                                .timeTable(timeTable)
                                .build())
                        .build())
                .poster(posterUrl != null ? new Image(posterUrl) : null)
                .detailImages(detailImages.stream().map(ImageVO::toDomain).toList())
                .sellers(sellers.stream().map(SellerVO::toDomain).collect(java.util.stream.Collectors.toSet()))
                .castNames(castNames)
                .build();
    }
}
