package com.backend.allreva.module.search.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.common.pagination.SliceResponse;
import com.backend.allreva.module.concert.concert.domain.Concert;
import com.backend.allreva.module.concert.concert.domain.ConcertRepository;
import com.backend.allreva.module.concert.concert.domain.value.ConcertInfo;
import com.backend.allreva.module.concert.concert.fixture.ConcertFixture;
import com.backend.allreva.module.concert.place.domain.ConcertHall;
import com.backend.allreva.module.concert.place.domain.ConcertHallRepository;
import com.backend.allreva.module.concert.place.fixture.ConcertHallFixture;
import com.backend.allreva.module.search.application.ConcertSearchService;
import com.backend.allreva.module.search.application.dto.ConcertThumbnail;
import com.backend.allreva.module.search.application.dto.SortDirection;
import com.backend.allreva.support.IntegrationTestSupport;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@SuppressWarnings("NonAsciiCharacters")
@DisplayName("Concert 검색 통합 테스트")
class ConcertSearchServiceTest extends IntegrationTestSupport {

    @Autowired
    ConcertSearchService concertSearchService;

    @Autowired
    ConcertRepository concertRepository;

    @Autowired
    ConcertHallRepository concertHallRepository;

    @AfterEach
    void tearDown() {
        concertRepository.deleteAll();
        concertHallRepository.deleteAll();
    }

    @Nested
    @DisplayName("콘서트 검색")
    class Describe_콘서트_검색 {

        @Nested
        @DisplayName("검색어로 콘서트를 조회할 때")
        class Context_검색어로_조회 {

            @Test
            @DisplayName("검색 결과가 반환된다")
            void 검색_결과가_반환된다() {
                // given
                String hallCode = "FCMOCK01-1";
                String concertCode = "PFMOCK001";
                concertHallRepository.save(Instancio.of(ConcertHallFixture.concertHallModel())
                        .set(field(ConcertHall.class, "hallCode"), hallCode)
                        .create());
                concertRepository.save(Instancio.of(ConcertFixture.inProgressConcertModel())
                        .set(field(Concert.class, "concertCode"), concertCode)
                        .set(field(Concert.class, "hallCode"), hallCode)
                        .set(field(ConcertInfo.class, "title"), "Sample Concert")
                        .create());

                // when
                List<ConcertThumbnail> result = concertSearchService.getConcertSuggestions("Sample");

                // then
                assertThat(result).isNotEmpty();
            }

            @Test
            @DisplayName("빈 검색어인 경우 예외가 발생한다")
            void 빈_검색어인_경우_예외가_발생한다() {
                assertThrows(CustomException.class, () -> concertSearchService.getConcertSuggestions(""));
            }
        }

        @Nested
        @DisplayName("cursor 페이지네이션 검색 조회 시")
        class Context_cursor_페이지네이션 {

            @Test
            @DisplayName("cursorId로 다음 페이지를 조회할 수 있다")
            void cursorId로_다음_페이지를_조회할_수_있다() {
                // given
                String hallCode = "FCMOCK01-1";
                concertHallRepository.save(Instancio.of(ConcertHallFixture.concertHallModel())
                        .set(field(ConcertHall.class, "hallCode"), hallCode)
                        .create());
                for (int i = 0; i < 3; i++) {
                    concertRepository.save(Instancio.of(ConcertFixture.inProgressConcertModel())
                            .set(field(Concert.class, "concertCode"), "PFMOCK00" + i)
                            .set(field(Concert.class, "hallCode"), hallCode)
                            .set(field(ConcertInfo.class, "title"), "Sample Concert")
                            .create());
                }

                // when
                SliceResponse<ConcertThumbnail, String> page1 = concertSearchService.searchConcerts("Sample", null, 2);
                SliceResponse<ConcertThumbnail, String> page2 =
                        concertSearchService.searchConcerts("Sample", page1.nextCursor(), 2);

                // then
                assertSoftly(softly -> {
                    softly.assertThat(page1.items()).hasSize(2);
                    softly.assertThat(page1.nextCursor()).isNotNull();
                    softly.assertThat(page2.items()).hasSize(1);
                    softly.assertThat(page2.items().get(0))
                            .isNotEqualTo(page1.items().get(0));
                });
            }

            @Test
            @DisplayName("검색 결과가 없는 경우 예외가 발생한다")
            void 검색_결과가_없는_경우_예외가_발생한다() {
                assertThrows(
                        CustomException.class, () -> concertSearchService.searchConcerts("존재하지않는검색어12345", null, 2));
            }
        }
    }

    @Nested
    @DisplayName("메인 페이지 조회")
    class Describe_메인_페이지_조회 {

        @Test
        @DisplayName("날짜 정렬로 메인 콘서트를 조회한다")
        void 날짜_정렬로_메인_콘서트를_조회한다() {
            // given
            String hallCode = "FCMOCK01-1";
            concertHallRepository.save(Instancio.of(ConcertHallFixture.concertHallModel())
                    .set(field(ConcertHall.class, "hallCode"), hallCode)
                    .create());
            for (int i = 0; i < 5; i++) {
                concertRepository.save(Instancio.of(ConcertFixture.inProgressConcertModel())
                        .set(field(Concert.class, "concertCode"), "PFMOCK00" + i)
                        .set(field(Concert.class, "hallCode"), hallCode)
                        .set(field(ConcertInfo.class, "title"), "Sample Concert")
                        .create());
            }

            // when
            SliceResponse<ConcertThumbnail, String> page1 =
                    concertSearchService.getMainConcerts("", null, 3, SortDirection.DATE);
            SliceResponse<ConcertThumbnail, String> page2 =
                    concertSearchService.getMainConcerts("", page1.nextCursor(), 3, SortDirection.DATE);

            // then
            assertSoftly(softly -> {
                softly.assertThat(page1.items()).hasSize(3);
                softly.assertThat(page2.items()).hasSize(2);
                softly.assertThat(page1.items()).doesNotContainAnyElementsOf(page2.items());
            });
        }
    }
}
