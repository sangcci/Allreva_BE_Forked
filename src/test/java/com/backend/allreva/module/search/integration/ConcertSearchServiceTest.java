package com.backend.allreva.module.search.integration;

import static com.backend.allreva.module.concert.place.fixture.ConcertHallFixture.createConcertHall;
import static com.backend.allreva.module.concert.concert.fixture.ConcertFixture.createTestConcert;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.module.concert.concert.domain.ConcertRepository;
import com.backend.allreva.module.concert.place.domain.ConcertHallRepository;
import com.backend.allreva.module.search.application.ConcertSearchService;
import com.backend.allreva.module.search.application.dto.ConcertMainResponse;
import com.backend.allreva.module.search.application.dto.ConcertSearchListResponse;
import com.backend.allreva.module.search.application.dto.ConcertThumbnail;
import com.backend.allreva.module.search.application.dto.SortDirection;
import com.backend.allreva.support.IntegrationTestSupport;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
                // given - ConcertFixture의 hallCode "123"과 일치하는 hall 저장
                concertHallRepository.save(createConcertHall("123"));
                concertRepository.save(createTestConcert()); // title: "Sample Concert"

                // when
                List<ConcertThumbnail> result = concertSearchService.searchConcertThumbnails("Sample");

                // then
                assertThat(result).isNotEmpty();
            }

            @Test
            @DisplayName("빈 검색어인 경우 예외가 발생한다")
            void 빈_검색어인_경우_예외가_발생한다() {
                assertThrows(CustomException.class, () ->
                        concertSearchService.searchConcertThumbnails(""));
            }
        }

        @Nested
        @DisplayName("cursor 페이지네이션 검색 조회 시")
        class Context_cursor_페이지네이션 {

            @Test
            @DisplayName("cursorId로 다음 페이지를 조회할 수 있다")
            void cursorId로_다음_페이지를_조회할_수_있다() {
                // given
                concertHallRepository.save(createConcertHall("123"));
                for (int i = 0; i < 3; i++) {
                    concertRepository.save(createTestConcert());
                }

                // when
                ConcertSearchListResponse page1 = concertSearchService.searchConcertList("Sample", null, 2);
                ConcertSearchListResponse page2 = concertSearchService.searchConcertList("Sample", page1.nextCursorId(), 2);

                // then
                assertSoftly(softly -> {
                    softly.assertThat(page1.concertThumbnails()).hasSize(2);
                    softly.assertThat(page1.nextCursorId()).isNotNull();
                    softly.assertThat(page2.concertThumbnails()).hasSize(1);
                    softly.assertThat(page2.concertThumbnails().get(0))
                            .isNotEqualTo(page1.concertThumbnails().get(0));
                });
            }

            @Test
            @DisplayName("검색 결과가 없는 경우 예외가 발생한다")
            void 검색_결과가_없는_경우_예외가_발생한다() {
                assertThrows(CustomException.class, () ->
                        concertSearchService.searchConcertList("존재하지않는검색어12345", null, 2));
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
            concertHallRepository.save(createConcertHall("123"));
            for (int i = 0; i < 5; i++) {
                concertRepository.save(createTestConcert());
            }

            // when
            ConcertMainResponse page1 = concertSearchService.searchMainConcerts("", null, 3, SortDirection.DATE);
            ConcertMainResponse page2 = concertSearchService.searchMainConcerts("", page1.nextCursorId(), 3, SortDirection.DATE);

            // then
            assertSoftly(softly -> {
                softly.assertThat(page1.concertThumbnails()).hasSize(3);
                softly.assertThat(page2.concertThumbnails()).hasSize(2);
                softly.assertThat(page1.concertThumbnails())
                        .doesNotContainAnyElementsOf(page2.concertThumbnails());
            });
        }
    }
}
