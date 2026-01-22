package com.backend.allreva.module.artist;

import com.backend.allreva.module.artist.application.ArtistService;
import com.backend.allreva.module.artist.application.dto.ArtistCreateRequest;
import com.backend.allreva.module.artist.application.dto.ArtistSearchResponse;
import com.backend.allreva.module.artist.application.port.ArtistSearchPort;
import com.backend.allreva.module.artist.domain.Artist;
import com.backend.allreva.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ArtistCommandServiceTest extends IntegrationTestSupport {
    @Autowired
    ArtistService artistService;

    @Autowired
    ArtistSearchPort artistSearchPort;

    @DisplayName("아티스트 검색후 값이 존재하지 않을 경우 DB에 삽입")
    @Test
    void saveIfNotExistTest() {
        // given
        String query = "day6";
        List<ArtistSearchResponse> responses = artistSearchPort.searchArtist(query);
        List<ArtistCreateRequest> artistCreateRequests = responses.stream()
                .map(response -> new ArtistCreateRequest(response.id(), response.name()))
                .toList();

        // when
        artistService.saveIfNotExist(artistCreateRequests);

        // then
        for (ArtistCreateRequest request : artistCreateRequests) {
            Artist savedArtist = artistService.getArtistById(request.artistId());
            assertThat(savedArtist)
                    .isNotNull()
                    .satisfies(artist -> {
                        assertThat(artist.getName()).isEqualTo(request.name());
                        assertThat(artist.getId()).isEqualTo(request.artistId());
                    });
        }
    }

}
