package com.backend.allreva.module.concert.concert.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.instancio.Select.field;

import com.backend.allreva.module.concert.concert.application.ConcertService;
import com.backend.allreva.module.concert.concert.application.dto.ConcertDetailResponse;
import com.backend.allreva.module.concert.concert.application.dto.RelatedConcertResponse;
import com.backend.allreva.module.concert.concert.domain.Concert;
import com.backend.allreva.module.concert.concert.domain.ConcertRepository;
import com.backend.allreva.module.concert.concert.fixture.ConcertFixture;
import com.backend.allreva.module.concert.place.domain.ConcertHall;
import com.backend.allreva.module.concert.place.domain.ConcertHallRepository;
import com.backend.allreva.module.concert.place.fixture.ConcertHallFixture;
import com.backend.allreva.support.IntegrationTestSupport;
import java.util.List;
import org.instancio.Instancio;
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
    @DisplayName("관련 공연 조회")
    class Describe_관련_공연_조회 {

        @Nested
        @DisplayName("같은 공연장의 공연이 여러 개 있을 때")
        class Context_같은_공연장_공연이_여러_개 {

            @Test
            @DisplayName("hallCode에 해당하는 공연만 반환된다")
            void hallCode에_해당하는_공연만_반환된다() {
                // given
                concertRepository.save(Instancio.of(ConcertFixture.inProgressConcertModel())
                        .set(field(Concert.class, "concertCode"), "PF0001")
                        .set(field(Concert.class, "hallCode"), "HALL-A")
                        .create());
                concertRepository.save(Instancio.of(ConcertFixture.inProgressConcertModel())
                        .set(field(Concert.class, "concertCode"), "PF0002")
                        .set(field(Concert.class, "hallCode"), "HALL-A")
                        .create());
                concertRepository.save(Instancio.of(ConcertFixture.inProgressConcertModel())
                        .set(field(Concert.class, "concertCode"), "PF0003")
                        .set(field(Concert.class, "hallCode"), "HALL-B")
                        .create());

                // when
                List<RelatedConcertResponse> result = concertService.getRelatedConcerts("HALL-A", null, 10);

                // then
                assertThat(result).hasSize(2);
                assertThat(result)
                        .extracting(RelatedConcertResponse::concertCode)
                        .containsExactlyInAnyOrder("PF0001", "PF0002");
            }

            @Test
            @DisplayName("pageSize만큼만 반환된다")
            void pageSize만큼만_반환된다() {
                // given
                concertRepository.save(Instancio.of(ConcertFixture.inProgressConcertModel())
                        .set(field(Concert.class, "concertCode"), "PF0001")
                        .set(field(Concert.class, "hallCode"), "HALL-C")
                        .create());
                concertRepository.save(Instancio.of(ConcertFixture.inProgressConcertModel())
                        .set(field(Concert.class, "concertCode"), "PF0002")
                        .set(field(Concert.class, "hallCode"), "HALL-C")
                        .create());
                concertRepository.save(Instancio.of(ConcertFixture.inProgressConcertModel())
                        .set(field(Concert.class, "concertCode"), "PF0003")
                        .set(field(Concert.class, "hallCode"), "HALL-C")
                        .create());

                // when
                List<RelatedConcertResponse> result = concertService.getRelatedConcerts("HALL-C", null, 2);

                // then
                assertThat(result).hasSize(2);
            }

            @Test
            @DisplayName("concertCode 내림차순으로 반환된다")
            void concertCode_내림차순으로_반환된다() {
                // given
                concertRepository.save(Instancio.of(ConcertFixture.inProgressConcertModel())
                        .set(field(Concert.class, "concertCode"), "PF0001")
                        .set(field(Concert.class, "hallCode"), "HALL-D")
                        .create());
                concertRepository.save(Instancio.of(ConcertFixture.inProgressConcertModel())
                        .set(field(Concert.class, "concertCode"), "PF0003")
                        .set(field(Concert.class, "hallCode"), "HALL-D")
                        .create());
                concertRepository.save(Instancio.of(ConcertFixture.inProgressConcertModel())
                        .set(field(Concert.class, "concertCode"), "PF0002")
                        .set(field(Concert.class, "hallCode"), "HALL-D")
                        .create());

                // when
                List<RelatedConcertResponse> result = concertService.getRelatedConcerts("HALL-D", null, 10);

                // then
                assertThat(result)
                        .extracting(RelatedConcertResponse::concertCode)
                        .containsExactly("PF0003", "PF0002", "PF0001");
            }
        }

        @Nested
        @DisplayName("커서 기반 페이징이 적용될 때")
        class Context_커서_페이징 {

            @Test
            @DisplayName("lastConcertCode보다 작은 concertCode의 공연만 반환된다")
            void lastConcertCode_이전_공연만_반환된다() {
                // given
                concertRepository.save(Instancio.of(ConcertFixture.inProgressConcertModel())
                        .set(field(Concert.class, "concertCode"), "PF0001")
                        .set(field(Concert.class, "hallCode"), "HALL-E")
                        .create());
                concertRepository.save(Instancio.of(ConcertFixture.inProgressConcertModel())
                        .set(field(Concert.class, "concertCode"), "PF0002")
                        .set(field(Concert.class, "hallCode"), "HALL-E")
                        .create());
                concertRepository.save(Instancio.of(ConcertFixture.inProgressConcertModel())
                        .set(field(Concert.class, "concertCode"), "PF0003")
                        .set(field(Concert.class, "hallCode"), "HALL-E")
                        .create());

                // when
                List<RelatedConcertResponse> result = concertService.getRelatedConcerts("HALL-E", "PF0003", 10);

                // then
                assertThat(result)
                        .extracting(RelatedConcertResponse::concertCode)
                        .containsExactly("PF0002", "PF0001");
            }
        }

        @Nested
        @DisplayName("poster가 없는 공연이 있을 때")
        class Context_poster가_없는_공연 {

            @Test
            @DisplayName("posterUrl이 null로 매핑된다")
            void posterUrl이_null로_매핑된다() {
                // given
                concertRepository.save(Instancio.of(ConcertFixture.inProgressConcertModel())
                        .set(field(Concert.class, "concertCode"), "PF0001")
                        .set(field(Concert.class, "hallCode"), "HALL-F")
                        .ignore(field(Concert.class, "poster"))
                        .create());

                // when
                List<RelatedConcertResponse> result = concertService.getRelatedConcerts("HALL-F", null, 10);

                // then
                assertSoftly(softly -> {
                    softly.assertThat(result).hasSize(1);
                    softly.assertThat(result.get(0).posterUrl()).isNull();
                });
            }
        }

        @Nested
        @DisplayName("해당 공연장의 공연이 없을 때")
        class Context_공연이_없을_때 {

            @Test
            @DisplayName("빈 리스트가 반환된다")
            void 빈_리스트가_반환된다() {
                // when
                List<RelatedConcertResponse> result = concertService.getRelatedConcerts("HALL-EMPTY", null, 10);

                // then
                assertThat(result).isEmpty();
            }
        }
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
                ConcertHall hall = concertHallRepository.save(
                        Instancio.of(ConcertHallFixture.concertHallModel()).create());
                Concert concert = concertRepository.save(Instancio.of(ConcertFixture.inProgressConcertModel())
                        .set(field(Concert.class, "hallCode"), hall.getHallCode())
                        .create());

                // when
                ConcertDetailResponse result = concertService.findDetailById(concert.getConcertCode());

                // then
                assertSoftly(softly -> {
                    softly.assertThat(result).isNotNull();
                    softly.assertThat(result.hallCode()).isEqualTo(hall.getHallCode());
                    softly.assertThat(result.hallName()).isEqualTo(hall.getName());
                    softly.assertThat(result.concertInfo().getTitle()).isEqualTo(concert.getConcertInfo().getTitle());
                    softly.assertThat(result.sellers()).isNotEmpty();
                    softly.assertThat(result.convenienceInfo()).isNotNull();
                });
            }
        }
    }
}
