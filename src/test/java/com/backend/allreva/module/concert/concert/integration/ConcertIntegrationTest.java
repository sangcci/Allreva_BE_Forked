package com.backend.allreva.module.concert.concert.integration;

import static com.backend.allreva.module.concert.concert.fixture.ConcertFixture.createConcertWithHallCode;
import static com.backend.allreva.module.concert.place.fixture.ConcertHallFixture.createTestConcertHall;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.backend.allreva.module.concert.concert.application.ConcertService;
import com.backend.allreva.module.concert.concert.application.dto.ConcertDetailResponse;
import com.backend.allreva.module.concert.concert.domain.Concert;
import com.backend.allreva.module.concert.concert.domain.ConcertRepository;
import com.backend.allreva.module.concert.place.domain.ConcertHall;
import com.backend.allreva.module.concert.place.domain.ConcertHallRepository;
import com.backend.allreva.support.IntegrationTestSupport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("Concert 통합 테스트")
class ConcertIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private ConcertService concertService;

    @Autowired
    private ConcertRepository concertRepository;

    @Autowired
    private ConcertHallRepository concertHallRepository;

    @AfterEach
    void tearDown() {
        concertRepository.deleteAll();
        concertHallRepository.deleteAll();
    }

    @Nested
    @DisplayName("공연 상세 조회")
    class Describe_공연_상세_조회 {

        @Nested
        @DisplayName("공연 ID로 조회할 때")
        class Context_공연_ID로_조회 {

            @Test
            @DisplayName("공연 상세 정보와 공연장 정보가 함께 반환된다")
            void 공연_상세_정보와_공연장_정보가_반환된다() {
                // given
                ConcertHall hall = concertHallRepository.save(createTestConcertHall());
                Concert concert = concertRepository.save(createConcertWithHallCode(hall.getId()));

                // when
                ConcertDetailResponse result = concertService.findDetailById(concert.getConcertCode());

                // then
                assertSoftly(softly -> {
                    softly.assertThat(result).isNotNull();
                    softly.assertThat(result.hallCode()).isEqualTo(hall.getId());
                    softly.assertThat(result.hallName()).isEqualTo(hall.getName());
                    softly.assertThat(result.concertInfo().getTitle()).isEqualTo("Sample Concert");
                    softly.assertThat(result.sellers()).isNotEmpty();
                    softly.assertThat(result.convenienceInfo()).isNotNull();
                });
            }
        }
    }
}
