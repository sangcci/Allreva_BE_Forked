package com.backend.allreva.concert.place.integration;

import com.backend.allreva.concert.place.domain.ConcertHallRepository;
import com.backend.allreva.support.IntegrationTestSupport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("Hall 통합 테스트")
class HallTestIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private ConcertHallRepository concertHallRepository;

    @AfterEach
    void tearDown() {
        concertHallRepository.deleteAll();
    }

    @Nested
    @DisplayName("공연장 상세 조회")
    class Describe_공연장_상세_조회 {

        @Test
        @DisplayName("공연장 코드로 조회할 때 상세 정보가 반환된다")
        void 공연장_코드로_조회할_때_상세_정보가_반환된다() {}
    }
}
