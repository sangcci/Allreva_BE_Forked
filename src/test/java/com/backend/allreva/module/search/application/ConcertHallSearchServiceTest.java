package com.backend.allreva.module.search.application;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.module.concert.hall.application.dto.ConcertHallMainResponse;
import com.backend.allreva.module.concert.hall.application.dto.ConcertHallThumbnail;
import com.backend.allreva.module.concert.hall.domain.ConcertHallDocument;
import com.backend.allreva.module.concert.hall.exception.ConcertHallErrorCode;
import com.backend.allreva.module.search.domain.ConcertHallSearchRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
@DisplayName("ConcertHallSearchService 단위 테스트")
class ConcertHallSearchServiceTest {

    @InjectMocks
    private ConcertHallSearchService concertHallSearchService;

    @Mock
    private ConcertHallSearchRepository concertHallSearchRepository;

    @Nested
    @DisplayName("메인 공연장 검색")
    class Describe_메인_공연장_검색 {

        @Nested
        @DisplayName("지역과 좌석 규모로 검색할 때")
        class Context_지역과_좌석_규모로_검색 {

            @Test
            @DisplayName("검색 결과가 반환된다")
            void 검색_결과가_반환된다() {
                // given
                String address = "서울";
                int seatScale = 1000;
                List<Object> searchAfter = new ArrayList<>();
                int size = 7;

                ConcertHallDocument hallDoc1 = createMockHallDocument("hall1", "서울 예술의전당", 2500);
                ConcertHallDocument hallDoc2 = createMockHallDocument("hall2", "세종문화회관", 1800);

                @SuppressWarnings("unchecked")
                SearchHit<ConcertHallDocument> hit1 = mock(SearchHit.class);
                @SuppressWarnings("unchecked")
                SearchHit<ConcertHallDocument> hit2 = mock(SearchHit.class);

                given(hit1.getContent()).willReturn(hallDoc1);
                given(hit2.getContent()).willReturn(hallDoc2);

                @SuppressWarnings("unchecked")
                SearchHits<ConcertHallDocument> searchHits = mock(SearchHits.class);
                given(searchHits.getSearchHits()).willReturn(List.of(hit1, hit2));
                given(searchHits.getTotalHits()).willReturn(2L);

                given(concertHallSearchRepository.searchMainConcertHall(
                        anyString(), anyInt(), anyList(), anyInt()))
                        .willReturn(searchHits);

                // when
                ConcertHallMainResponse result = concertHallSearchService.searchMainConcertHalls(
                        address, seatScale, searchAfter, size);

                // then
                assertSoftly(softly -> {
                    softly.assertThat(result).isNotNull();
                    softly.assertThat(result.concertHallThumbnails()).hasSize(2);
                    softly.assertThat(result.concertHallThumbnails().get(0).name()).isEqualTo("서울 예술의전당");
                    softly.assertThat(result.concertHallThumbnails().get(1).name()).isEqualTo("세종문화회관");
                });
                verify(concertHallSearchRepository, times(1))
                        .searchMainConcertHall(address, seatScale, searchAfter, size + 1);
            }

            @Test
            @DisplayName("검색 결과가 없으면 예외가 발생한다")
            void 검색_결과가_없으면_예외가_발생한다() {
                // given
                String address = "제주";
                int seatScale = 5000;
                List<Object> searchAfter = new ArrayList<>();
                int size = 7;

                @SuppressWarnings("unchecked")
                SearchHits<ConcertHallDocument> searchHits = mock(SearchHits.class);
                given(searchHits.getSearchHits()).willReturn(List.of());
                given(searchHits.getTotalHits()).willReturn(0L);

                given(concertHallSearchRepository.searchMainConcertHall(
                        anyString(), anyInt(), anyList(), anyInt()))
                        .willReturn(searchHits);

                // when & then
                assertThatThrownBy(() ->
                        concertHallSearchService.searchMainConcertHalls(address, seatScale, searchAfter, size))
                        .isInstanceOf(CustomException.class)
                        .hasFieldOrPropertyWithValue("errorCode", ConcertHallErrorCode.CONCERT_HALL_SEARCH_NOTFOUND);
            }

            @Test
            @DisplayName("다음 페이지가 있으면 searchAfter가 반환된다")
            void 다음_페이지가_있으면_searchAfter가_반환된다() {
                // given
                String address = "서울";
                int seatScale = 1000;
                List<Object> searchAfter = new ArrayList<>();
                int size = 2;

                ConcertHallDocument hallDoc1 = createMockHallDocument("hall1", "홀1", 1500);
                ConcertHallDocument hallDoc2 = createMockHallDocument("hall2", "홀2", 1600);

                @SuppressWarnings("unchecked")
                SearchHit<ConcertHallDocument> hit1 = mock(SearchHit.class);
                @SuppressWarnings("unchecked")
                SearchHit<ConcertHallDocument> hit2 = mock(SearchHit.class);
                @SuppressWarnings("unchecked")
                SearchHit<ConcertHallDocument> hit3 = mock(SearchHit.class);

                given(hit1.getContent()).willReturn(hallDoc1);
                given(hit2.getContent()).willReturn(hallDoc2);
                given(hit2.getSortValues()).willReturn(List.of("sort2"));

                @SuppressWarnings("unchecked")
                SearchHits<ConcertHallDocument> searchHits = mock(SearchHits.class);
                given(searchHits.getSearchHits()).willReturn(List.of(hit1, hit2, hit3));
                given(searchHits.getTotalHits()).willReturn(3L);

                given(concertHallSearchRepository.searchMainConcertHall(
                        anyString(), anyInt(), anyList(), anyInt()))
                        .willReturn(searchHits);

                // when
                ConcertHallMainResponse result = concertHallSearchService.searchMainConcertHalls(
                        address, seatScale, searchAfter, size);

                // then
                assertSoftly(softly -> {
                    softly.assertThat(result.concertHallThumbnails()).hasSize(2);
                    softly.assertThat(result.searchAfter()).isNotNull();
                    softly.assertThat(result.searchAfter()).isEqualTo(List.of("sort2"));
                });
            }
        }
    }

    private ConcertHallDocument createMockHallDocument(String id, String name, int seatScale) {
        ConcertHallDocument doc = mock(ConcertHallDocument.class);
        given(doc.getId()).willReturn(id);
        given(doc.getName()).willReturn(name);
        given(doc.getSeatScale()).willReturn(seatScale);
        given(doc.getAddress()).willReturn("서울특별시");
        given(doc.getParking()).willReturn(true);
        given(doc.getRestaurant()).willReturn(true);
        given(doc.getCafe()).willReturn(true);
        given(doc.getStore()).willReturn(false);
        given(doc.getParkBarrier()).willReturn(true);
        given(doc.getRestBarrier()).willReturn(true);
        given(doc.getElevBarrier()).willReturn(true);
        given(doc.getRunwBarrier()).willReturn(false);
        return doc;
    }
}
