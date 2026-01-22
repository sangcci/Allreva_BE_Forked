package com.backend.allreva.module.artist;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.module.artist.application.dto.ArtistSearchResponse;
import com.backend.allreva.module.artist.application.port.ArtistSearchPort;
import com.backend.allreva.support.IntegrationTestSupport;

class ArtistQueryServiceTest extends IntegrationTestSupport {

    @Autowired
    ArtistSearchPort artistSearchPort;

    @DisplayName("아티스트 검색에 성공한다.")
    @Test
    void searchArtistTest() {
        // given
        String query = "하현상";

        // when
        List<ArtistSearchResponse> responses = artistSearchPort.searchArtist(query);

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
                () -> artistSearchPort.searchArtist(query));
    }
}
