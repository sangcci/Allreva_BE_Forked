package com.backend.allreva.recruitment.rent;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.backend.allreva.common.config.QuerydslConfig;
import com.backend.allreva.concert.concert.ConcertEntity;
import com.backend.allreva.concert.concert.fixture.ConcertFixture;
import com.backend.allreva.recruitment.rent.domain.BusSize;
import com.backend.allreva.recruitment.rent.domain.SortType;
import com.backend.allreva.recruitment.rent.fixture.RentFixture;
import com.backend.allreva.recruitment.rent.query.model.RentDetail;
import com.backend.allreva.recruitment.rent.query.model.RentSummary;
import com.backend.allreva.support.DataJpaTestSupport;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

@Import({QuerydslConfig.class, RentFinderAdapter.class})
@DisplayName("RentFinderAdapter 테스트")
class RentFinderAdapterTest extends DataJpaTestSupport {

    @Autowired
    private RentFinderAdapter rentFinderAdapter;

    @Test
    @DisplayName("차대절 상세 조회 시 공연과 차대절 projection을 반환한다")
    void find_rent_detail() {
        // given
        entityManager.persist(ConcertEntity.from(ConcertFixture.createConcert()));
        RentEntity rent = entityManager.persist(RentEntity.from(RentFixture.createRent()));
        entityManager.flush();
        entityManager.clear();

        // when
        RentDetail detail = rentFinderAdapter.findRentDetail(rent.getId()).orElseThrow();

        // then
        assertSoftly(softly -> {
            softly.assertThat(detail.concertName()).isEqualTo(ConcertFixture.TITLE);
            softly.assertThat(detail.imageUrl()).isEqualTo(RentFixture.IMAGE_URL);
            softly.assertThat(detail.title()).isEqualTo(RentFixture.TITLE);
            softly.assertThat(detail.upRoute().getBoardingArea()).isEqualTo("서울역");
            softly.assertThat(detail.downRoute().getDropOffArea()).isEqualTo("서울역");
            softly.assertThat(detail.busSize()).isEqualTo(BusSize.LARGE);
            softly.assertThat(detail.maxPassenger()).isEqualTo(45);
            softly.assertThat(detail.boardingDates()).hasSize(1);
            softly.assertThat(detail.recruitmentCount()).isEqualTo(45);
        });
    }

    @Test
    @DisplayName("차대절 목록 조회 시 route와 image projection을 반환한다")
    void find_rent_summaries() {
        // given
        RentEntity rent = entityManager.persist(RentEntity.from(RentFixture.createRent()));
        entityManager.flush();
        entityManager.clear();

        // when
        List<RentSummary> summaries = rentFinderAdapter.findRentSummaries(null, SortType.LATEST, null, null, 10);

        // then
        assertSoftly(softly -> {
            softly.assertThat(summaries).hasSize(1);
            RentSummary summary = summaries.get(0);
            softly.assertThat(summary.rentId()).isEqualTo(rent.getId());
            softly.assertThat(summary.title()).isEqualTo(RentFixture.TITLE);
            softly.assertThat(summary.imageUrl()).isEqualTo(RentFixture.IMAGE_URL);
            softly.assertThat(summary.upRoute().getBoardingArea()).isEqualTo("서울역");
            softly.assertThat(summary.downRoute().getDropOffArea()).isEqualTo("서울역");
        });
    }
}
