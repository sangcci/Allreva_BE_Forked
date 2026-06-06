package com.backend.allreva.concert.concert.domain;

import com.backend.allreva.common.model.Image;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Concert {

    private String concertCode;
    private String hallCode;
    private ConcertInfo concertInfo;
    private Image poster;
    private List<Image> detailImages = new ArrayList<>();
    private Set<Seller> sellers = new HashSet<>();
    private List<String> castNames = new ArrayList<>();

    public boolean isValidBoardingDate(final LocalDate date) {
        LocalDate startDate = concertInfo.getDateInfo().getStartDate();
        LocalDate endDate = concertInfo.getDateInfo().getEndDate();
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    public void updateFrom(final Concert fetched) {
        this.hallCode = fetched.hallCode;
        this.concertInfo = fetched.concertInfo;
        this.poster = fetched.poster;
        this.detailImages = fetched.detailImages;
        this.sellers = fetched.sellers;
        this.castNames = fetched.castNames;
    }

    @Builder
    private Concert(
            final String concertCode,
            final String hallCode,
            final ConcertInfo concertInfo,
            final Image poster,
            final List<Image> detailImages,
            final Set<Seller> sellers,
            final List<String> castNames) {
        this.concertCode = concertCode;
        this.hallCode = hallCode;
        this.concertInfo = concertInfo;
        this.poster = poster;
        this.detailImages = detailImages != null ? detailImages : this.detailImages;
        this.sellers = sellers != null ? sellers : this.sellers;
        this.castNames = castNames != null ? castNames : this.castNames;
    }
}
