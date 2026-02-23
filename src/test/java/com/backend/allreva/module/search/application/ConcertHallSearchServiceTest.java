package com.backend.allreva.module.search.application;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.module.concert.hall.application.dto.ConcertHallMainResponse;
import com.backend.allreva.module.concert.hall.application.dto.ConcertHallThumbnail;
import com.backend.allreva.module.concert.hall.domain.ConvenienceInfo;
import com.backend.allreva.module.concert.hall.exception.ConcertHallErrorCode;
import com.backend.allreva.module.search.domain.ConcertHallSearchRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
@DisplayName("ConcertHallSearchService 단위 테스트")
class ConcertHallSearchServiceTest {

    @InjectMocks
    private ConcertHallSearchService concertHallSearchService;

    @Mock
    private ConcertHallSearchRepository concertHallSearchRepository;

    private ConcertHallThumbnail createThumbnail(String id, String name, int seatScale) {
        return new ConcertHallThumbnail(id, name, "서울특별시", seatScale,
                ConvenienceInfo.builder()
                        .hasParkingLot(true).hasRestaurant(true)
                        .hasCafe(true).hasDisabledParking(true)
                        .build());
    }

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
                String cursorId = null;
                int size = 7;

                ConcertHallThumbnail thumb1 = createThumbnail("hall1", "서울 예술의전당", 2500);
                ConcertHallThumbnail thumb2 = createThumbnail("hall2", "세종문화회관", 1800);
                ConcertHallMainResponse mockResponse = ConcertHallMainResponse.from(
                        List.of(thumb1, thumb2), null);

                given(concertHallSearchRepository.searchMain(address, seatScale, cursorId, size))
                        .willReturn(mockResponse);

                // when
                ConcertHallMainResponse result = concertHallSearchService.searchMainConcertHalls(
                        address, seatScale, cursorId, size);

                // then
                assertSoftly(softly -> {
                    softly.assertThat(result).isNotNull();
                    softly.assertThat(result.concertHallThumbnails()).hasSize(2);
                    softly.assertThat(result.concertHallThumbnails().get(0).name()).isEqualTo("서울 예술의전당");
                    softly.assertThat(result.concertHallThumbnails().get(1).name()).isEqualTo("세종문화회관");
                });
            }

            @Test
            @DisplayName("검색 결과가 없으면 예외가 발생한다")
            void 검색_결과가_없으면_예외가_발생한다() {
                // given
                String address = "제주";
                int seatScale = 5000;
                String cursorId = null;
                int size = 7;

                ConcertHallMainResponse mockResponse = ConcertHallMainResponse.from(List.of(), null);
                given(concertHallSearchRepository.searchMain(address, seatScale, cursorId, size))
                        .willReturn(mockResponse);

                // when & then
                assertThatThrownBy(() ->
                        concertHallSearchService.searchMainConcertHalls(address, seatScale, cursorId, size))
                        .isInstanceOf(CustomException.class)
                        .hasFieldOrPropertyWithValue("errorCode", ConcertHallErrorCode.CONCERT_HALL_SEARCH_NOTFOUND);
            }

            @Test
            @DisplayName("다음 페이지가 있으면 nextCursorId가 반환된다")
            void 다음_페이지가_있으면_nextCursorId가_반환된다() {
                // given
                String address = "서울";
                int seatScale = 1000;
                String cursorId = null;
                int size = 2;

                ConcertHallThumbnail thumb1 = createThumbnail("hall1", "홀1", 1500);
                ConcertHallThumbnail thumb2 = createThumbnail("hall2", "홀2", 1600);
                ConcertHallMainResponse mockResponse = ConcertHallMainResponse.from(
                        List.of(thumb1, thumb2), "hall2");

                given(concertHallSearchRepository.searchMain(address, seatScale, cursorId, size))
                        .willReturn(mockResponse);

                // when
                ConcertHallMainResponse result = concertHallSearchService.searchMainConcertHalls(
                        address, seatScale, cursorId, size);

                // then
                assertSoftly(softly -> {
                    softly.assertThat(result.concertHallThumbnails()).hasSize(2);
                    softly.assertThat(result.nextCursorId()).isEqualTo("hall2");
                });
            }
        }
    }
}
