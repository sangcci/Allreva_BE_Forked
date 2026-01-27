package com.backend.allreva.module.search.integration;

import static com.backend.allreva.module.concert.hall.fixture.ConcertHallFixture.createTestConcertHall;

import static com.backend.allreva.module.concert.concert.fixture.ConcertFixture.createTestConcert;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.module.concert.concert.infra.ConcertRepository;
import com.backend.allreva.module.concert.hall.domain.ConcertHallRepository;
import com.backend.allreva.module.search.application.ConcertSearchService;
import com.backend.allreva.module.search.application.dto.ConcertMainResponse;
import com.backend.allreva.module.search.application.dto.ConcertSearchListResponse;
import com.backend.allreva.module.search.application.dto.ConcertThumbnail;
import com.backend.allreva.module.search.domain.SortDirection;
import com.backend.allreva.support.IntegrationTestSupport;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
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
                // given
                ConcertMainResponse concertMain = concertSearchService.searchMainConcerts("", null, 1, SortDirection.DATE);
                String query = concertMain.concertThumbnails().get(0).title();

                // when
                log.info("query: {}", query);
                List<ConcertThumbnail> result = concertSearchService.searchConcertThumbnails(query);

                // then
                assertThat(result).isNotEmpty();
            }

            @Test
            @DisplayName("빈 검색어인 경우 예외가 발생한다")
            void 빈_검색어인_경우_예외가_발생한다() {
                // when & then
                assertThrows(CustomException.class, () -> {
                    concertSearchService.searchConcertThumbnails("");
                });
            }
        }

        @Nested
        @DisplayName("페이지네이션을 사용한 검색 조회 시")
        class Context_페이지네이션_검색_조회 {

            @Test
            @DisplayName("searchAfter를 사용하여 다음 페이지를 조회할 수 있다")
            void searchAfter를_사용하여_다음_페이지를_조회할_수_있다() {
                // given
                List<Object> searchAfter = new ArrayList<>();
                ConcertMainResponse concertMain = concertSearchService.searchMainConcerts("", null, 2, SortDirection.DATE);
                String query = concertMain.concertThumbnails().get(0).title() + concertMain.concertThumbnails().get(1).title();

                // when
                ConcertSearchListResponse response1 = concertSearchService.searchConcertList(query, searchAfter, 1);
                log.info("response1: {}", response1);
                ConcertSearchListResponse response2 = concertSearchService.searchConcertList(query, response1.searchAfter(), 1);
                log.info("response2: {}", response2);
                ConcertSearchListResponse response3 = concertSearchService.searchConcertList(query, searchAfter, 2);
                log.info("response3: {}", response3);

                // then
                assertThat(response2.concertThumbnails().get(0))
                        .usingRecursiveComparison()
                        .isEqualTo(response3.concertThumbnails().get(1));
            }

            @Test
            @DisplayName("검색 결과가 없는 경우 예외가 발생한다")
            void 검색_결과가_없는_경우_예외가_발생한다() {
                // given
                List<Object> searchAfter = new ArrayList<>();
                String query = "|||";

                // when & then
                assertThrows(CustomException.class, () -> {
                    concertSearchService.searchConcertList(query, searchAfter, 2);
                });
            }
        }
    }

    @Nested
    @DisplayName("메인 페이지 조회")
    class Describe_메인_페이지_조회 {

        @Nested
        @DisplayName("searchAfter를 사용한 페이지네이션 조회 시")
        class Context_페이지네이션_조회 {

            @Test
            @DisplayName("다음 페이지 조회가 정상적으로 동작한다")
            void 다음_페이지_조회가_정상적으로_동작한다() {
                // given
                concertHallRepository.save(createTestConcertHall());
                for (int i = 0; i < 10; i++) {
                    concertRepository.save(createTestConcert());
                }

                List<Object> searchAfter = new ArrayList<>();

                // when - 첫 페이지 조회 (3개)
                ConcertMainResponse page1 = concertSearchService.searchMainConcerts(
                        "", searchAfter, 3, SortDirection.VIEWS);

                // then
                assertSoftly(softly -> {
                    softly.assertThat(page1.concertThumbnails()).hasSize(3);
                    softly.assertThat(page1.searchAfter()).isNotNull();
                });

                // when - 두 번째 페이지 조회
                ConcertMainResponse page2 = concertSearchService.searchMainConcerts(
                        "", page1.searchAfter(), 3, SortDirection.VIEWS);

                // then
                assertSoftly(softly -> {
                    softly.assertThat(page2.concertThumbnails()).hasSize(3);
                    // 첫 페이지와 두 번째 페이지의 결과가 다름
                    softly.assertThat(page2.concertThumbnails()).isNotEqualTo(page1.concertThumbnails());
                });
            }

            @Test
            @DisplayName("전체 조회 시 searchAfter 결과와 일치한다")
            void 전체_조회시_searchAfter_결과와_일치한다() {
                // given
                concertHallRepository.save(createTestConcertHall());
                for (int i = 0; i < 6; i++) {
                    concertRepository.save(createTestConcert());
                }

                List<Object> searchAfter = new ArrayList<>();

                // when
                ConcertMainResponse page1 = concertSearchService.searchMainConcerts(
                        "", searchAfter, 3, SortDirection.VIEWS);
                ConcertMainResponse fullPage = concertSearchService.searchMainConcerts(
                        "", searchAfter, 6, SortDirection.VIEWS);

                // then - 첫 페이지의 마지막 항목이 전체 조회의 해당 위치 항목과 일치
                assertThat(fullPage.concertThumbnails().get(0).title())
                        .isEqualTo(page1.concertThumbnails().get(0).title());
            }
        }
    }
}
