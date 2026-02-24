package com.backend.allreva.module.recruitment.rent.domain;

import com.backend.allreva.common.model.BaseEntity;
import com.backend.allreva.common.model.Image;
import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.module.recruitment.rent.domain.value.Bus;
import com.backend.allreva.module.recruitment.rent.domain.value.Price;
import com.backend.allreva.module.recruitment.rent.domain.value.RefundType;
import com.backend.allreva.module.recruitment.rent.domain.value.Region;
import com.backend.allreva.module.recruitment.rent.exception.RentErrorCode;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLRestriction("deleted_at is NULL")
@SQLDelete(sql = "UPDATE rent SET deleted_at = NOW() WHERE id = ?")
@Entity
@Table(name = "rent")
public class Rent extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private Long concertId;

    @Column(nullable = false)
    private String title;

    @Embedded
    @AttributeOverride(name = "url", column = @Column(name = "image", nullable = false))
    private Image image;

    @Column(nullable = false)
    private String artistName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Region region;

    @Column(nullable = false)
    private String depositAccount;

    @Column(nullable = false)
    private String boardingArea;

    @Column(nullable = false)
    private String upTime;

    @Column(nullable = false)
    private String downTime;

    @Embedded
    private Bus bus;

    @Embedded
    private Price price;

    @Column(name = "eddate", nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private String chatUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RefundType refundType;

    private String information;

    @Builder.Default
    @OneToMany(mappedBy = "rent", cascade = CascadeType.ALL)
    private List<RentBoardingInfo> boardingInfos = new ArrayList<>();

    @Builder.Default
    @Column(nullable = false)
    private boolean isClosed = false;

    public void assignBoardingInfos(List<RentBoardingInfo> boardingInfos) {
        boardingInfos.forEach(boardingInfo -> boardingInfo.assignRent(this));
        this.boardingInfos = boardingInfos;
    }

    public void updateRent(
            final String boardingArea,
            final String upTime,
            final String downTime,
            final Image image,
            final Region region,
            final Bus bus,
            final Price price,
            final LocalDate endDate,
            final String chatUrl,
            final RefundType refundType,
            final String information,
            final List<RentBoardingInfo> newBoardingInfos) {
        this.image = image;
        this.region = region;
        this.boardingArea = boardingArea;
        this.upTime = upTime;
        this.downTime = downTime;
        this.bus = bus;
        this.price = price;
        this.endDate = endDate;
        this.chatUrl = chatUrl;
        this.refundType = refundType;
        this.information = information;
        assignBoardingInfos(newBoardingInfos);
        this.isClosed = false;
    }

    public void validateMine(Long memberId) {
        if (!this.memberId.equals(memberId)) {
            throw new CustomException(RentErrorCode.RENT_ACCESS_DENIED);
        }
    }

    public void close() {
        isClosed = true;
    }
}
