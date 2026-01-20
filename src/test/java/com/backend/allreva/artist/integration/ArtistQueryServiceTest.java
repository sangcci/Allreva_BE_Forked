package com.backend.allreva.artist.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.backend.allreva.artist.query.application.ArtistQueryService;
import com.backend.allreva.artist.query.application.response.SpotifySearchResponse;
import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.support.IntegrationTestSupport;

class ArtistQueryServiceTest extends IntegrationTestSupport {

    @Autowired
    ArtistQueryService artistQueryService;

    @DisplayName("아티스트 검색에 성공한다.")
    @Test
    void searchArtistTest() {
        // given
        String query = "하현상";

        // when
        List<SpotifySearchResponse> responses = artistQueryService.searchArtist(query);

        // then
        assertThat(responses).isNotEmpty();
    }

    @DisplayName("아티스트 검색 결과가 없을 시 예외 반환")
    @Test
    void searchArtistExceptionTest() {
        // given
        String query = "데이삭스";

        // when
        // then
        Assertions.assertThrows(CustomException.class,
                () -> artistQueryService.searchArtist(query));
    }
}
