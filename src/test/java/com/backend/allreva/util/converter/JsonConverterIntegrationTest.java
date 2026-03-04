package com.backend.allreva.util.converter;

import static com.backend.allreva.module.concert.concert.fixture.ConcertFixture.createTestConcert;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.backend.allreva.module.concert.concert.domain.Concert;
import com.backend.allreva.module.concert.concert.domain.ConcertRepository;
import com.backend.allreva.support.IntegrationTestSupport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("JsonConverter 통합 테스트")
class JsonConverterIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private ConcertRepository concertRepository;

    @AfterEach
    void tearDown() {
        concertRepository.deleteAll();
    }

    @Nested
    @DisplayName("List<Image> jsonb 변환")
    class Describe_List_Image_jsonb_변환 {

        @Nested
        @DisplayName("Concert를 저장할 때")
        class Context_Concert_저장 {

            @Test
            @DisplayName("detailImages가 jsonb 컬럼에 정상 저장되고 조회된다")
            void detailImages_저장_조회() {
                // given
                Concert concert = createTestConcert();

                // when
                Concert saved = concertRepository.save(concert);

                // then
                Concert loaded = concertRepository.findById(saved.getId()).orElseThrow();
                assertSoftly(softly -> {
                    softly.assertThat(loaded.getDetailImages())
                            .hasSize(concert.getDetailImages().size());
                    softly.assertThat(loaded.getDetailImages())
                            .containsExactlyInAnyOrderElementsOf(concert.getDetailImages());
                });
            }
        }
    }

    @Nested
    @DisplayName("Set<Seller> jsonb 변환")
    class Describe_Set_Seller_jsonb_변환 {

        @Nested
        @DisplayName("Concert를 저장할 때")
        class Context_Concert_저장 {

            @Test
            @DisplayName("sellers가 jsonb 컬럼에 정상 저장되고 조회된다")
            void sellers_저장_조회() {
                // given
                Concert concert = createTestConcert();

                // when
                Concert saved = concertRepository.save(concert);

                // then
                Concert loaded = concertRepository.findById(saved.getId()).orElseThrow();
                assertSoftly(softly -> {
                    softly.assertThat(loaded.getSellers())
                            .hasSize(concert.getSellers().size());
                    softly.assertThat(loaded.getSellers()).containsExactlyInAnyOrderElementsOf(concert.getSellers());
                });
            }
        }
    }
}
