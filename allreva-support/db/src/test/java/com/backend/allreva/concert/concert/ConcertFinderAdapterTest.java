package com.backend.allreva.concert.concert;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.backend.allreva.common.config.QuerydslConfig;
import com.backend.allreva.common.model.Image;
import com.backend.allreva.concert.concert.fixture.ConcertFixture;
import com.backend.allreva.concert.concert.query.model.ConcertDetail;
import com.backend.allreva.concert.place.ConcertHallEntity;
import com.backend.allreva.concert.place.fixture.ConcertHallFixture;
import com.backend.allreva.support.DataJpaTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

@Import({QuerydslConfig.class, ConcertFinderAdapter.class})
@DisplayName("ConcertFinderAdapter 테스트")
class ConcertFinderAdapterTest extends DataJpaTestSupport {

    @Autowired
    private ConcertFinderAdapter concertFinderAdapter;

    @Test
    @DisplayName("공연 상세 조회 시 공연과 공연장 projection을 반환한다")
    void find_concert_detail() {
        // given
        entityManager.persist(ConcertHallEntity.from(ConcertHallFixture.createConcertHall()));
        entityManager.persist(ConcertEntity.from(ConcertFixture.createConcert()));
        entityManager.flush();
        entityManager.clear();

        // when
        ConcertDetail detail = concertFinderAdapter
                .findConcertDetail(ConcertFixture.CONCERT_CODE)
                .orElseThrow();

        // then
        assertSoftly(softly -> {
            softly.assertThat(detail.poster().getUrl()).isEqualTo(ConcertFixture.POSTER_URL);
            softly.assertThat(detail.detailImages()).extracting(Image::getUrl).containsExactly("detail-1.jpg");
            softly.assertThat(detail.concertInfo().getTitle()).isEqualTo(ConcertFixture.TITLE);
            softly.assertThat(detail.hallCode()).isEqualTo(ConcertHallFixture.HALL_CODE);
            softly.assertThat(detail.hallName()).isEqualTo(ConcertHallFixture.NAME);
            softly.assertThat(detail.convenienceInfo().isHasParkingLot()).isTrue();
            softly.assertThat(detail.address()).isEqualTo(ConcertHallFixture.ADDRESS);
        });
    }
}
