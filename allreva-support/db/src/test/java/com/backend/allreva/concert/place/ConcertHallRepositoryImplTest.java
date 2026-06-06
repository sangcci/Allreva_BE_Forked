package com.backend.allreva.concert.place;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.backend.allreva.concert.place.domain.ConcertHall;
import com.backend.allreva.concert.place.fixture.ConcertHallFixture;
import com.backend.allreva.support.DataJpaTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

@Import(ConcertHallRepositoryImpl.class)
@DisplayName("ConcertHallRepositoryImpl 테스트")
class ConcertHallRepositoryImplTest extends DataJpaTestSupport {

    @Autowired
    private ConcertHallRepositoryImpl concertHallRepository;

    @Test
    @DisplayName("공연장을 저장하고 id로 조회한다")
    void save_and_find_by_id() {
        // given
        ConcertHall hall = ConcertHallFixture.createConcertHall();

        // when
        concertHallRepository.save(hall);
        entityManager.flush();
        entityManager.clear();
        ConcertHall found =
                concertHallRepository.findById(ConcertHallFixture.HALL_CODE).orElseThrow();

        // then
        assertSoftly(softly -> {
            softly.assertThat(found.getName()).isEqualTo(ConcertHallFixture.NAME);
            softly.assertThat(found.getConvenienceInfo().isHasParkingLot()).isTrue();
            softly.assertThat(found.getConvenienceInfo().isHasCafe()).isFalse();
            softly.assertThat(found.getLocation().getLongitude()).isEqualTo(127.1);
            softly.assertThat(found.getLocation().getAddress()).isEqualTo(ConcertHallFixture.ADDRESS);
        });
    }
}
