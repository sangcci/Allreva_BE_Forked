package com.backend.allreva.module.artist.application;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.module.artist.application.dto.ArtistCreateRequest;
import com.backend.allreva.module.artist.domain.Artist;
import com.backend.allreva.module.artist.domain.ArtistRepository;
import com.backend.allreva.module.artist.exception.ArtistErrorCode;
import com.backend.allreva.module.artist.fixture.ArtistFixture;
import com.backend.allreva.module.artist.fixture.ArtistRequestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
@DisplayName("ArtistService 단위 테스트")
class ArtistServiceTest {

    @InjectMocks
    private ArtistService artistService;

    @Mock
    private ArtistRepository artistRepository;

    @Nested
    @DisplayName("아티스트 저장")
    class Describe_아티스트_저장 {

        @Nested
        @DisplayName("새로운 아티스트 목록을 저장할 때")
        class Context_새로운_아티스트_목록_저장 {

            @Test
            @DisplayName("모든 아티스트가 성공적으로 저장된다")
            void 모든_아티스트가_저장된다() {
                // given
                List<ArtistCreateRequest> requests = ArtistRequestFixture.createArtistCreateRequests();
                List<String> artistIds = requests.stream()
                        .map(ArtistCreateRequest::artistId)
                        .toList();

                given(artistRepository.findAllById(artistIds)).willReturn(List.of());
                given(artistRepository.saveAll(anyList())).willAnswer(invocation -> invocation.getArgument(0));

                // when
                artistService.saveIfNotExist(requests);

                // then
                verify(artistRepository, times(1)).findAllById(artistIds);
                verify(artistRepository, times(1)).saveAll(argThat(artists -> {
                    List<Artist> artistList = (List<Artist>) artists;
                    return artistList.size() == 3;
                }));
            }
        }

        @Nested
        @DisplayName("이미 존재하는 아티스트가 포함된 목록을 저장할 때")
        class Context_이미_존재하는_아티스트_포함_저장 {

            @Test
            @DisplayName("존재하지 않는 아티스트만 저장된다")
            void 존재하지_않는_아티스트만_저장된다() {
                // given
                List<ArtistCreateRequest> requests = List.of(
                        ArtistRequestFixture.createArtistCreateRequest("artist-1", "아티스트1"),
                        ArtistRequestFixture.createArtistCreateRequest("artist-2", "아티스트2"),
                        ArtistRequestFixture.createArtistCreateRequest("artist-3", "아티스트3")
                );
                List<String> artistIds = List.of("artist-1", "artist-2", "artist-3");

                // artist-1은 이미 존재
                Artist existingArtist = ArtistFixture.createArtist("artist-1", "아티스트1");
                given(artistRepository.findAllById(artistIds)).willReturn(List.of(existingArtist));
                given(artistRepository.saveAll(anyList())).willAnswer(invocation -> invocation.getArgument(0));

                // when
                artistService.saveIfNotExist(requests);

                // then
                verify(artistRepository, times(1)).findAllById(artistIds);
                verify(artistRepository, times(1)).saveAll(argThat(artists -> {
                    List<Artist> artistList = (List<Artist>) artists;
                    return artistList.size() == 2 &&
                            artistList.stream().noneMatch(artist -> artist.getId().equals("artist-1"));
                }));
            }
        }

        @Nested
        @DisplayName("모든 아티스트가 이미 존재할 때")
        class Context_모든_아티스트가_이미_존재 {

            @Test
            @DisplayName("새로운 아티스트가 저장되지 않는다")
            void 새로운_아티스트가_저장되지_않는다() {
                // given
                List<ArtistCreateRequest> requests = List.of(
                        ArtistRequestFixture.createArtistCreateRequest("artist-1", "아티스트1"),
                        ArtistRequestFixture.createArtistCreateRequest("artist-2", "아티스트2")
                );
                List<String> artistIds = List.of("artist-1", "artist-2");

                List<Artist> existingArtists = List.of(
                        ArtistFixture.createArtist("artist-1", "아티스트1"),
                        ArtistFixture.createArtist("artist-2", "아티스트2")
                );
                given(artistRepository.findAllById(artistIds)).willReturn(existingArtists);

                // when
                artistService.saveIfNotExist(requests);

                // then
                verify(artistRepository, times(1)).findAllById(artistIds);
                verify(artistRepository, times(1)).saveAll(argThat(artists -> {
                    List<Artist> artistList = (List<Artist>) artists;
                    return artistList.isEmpty();
                }));
            }
        }

        @Nested
        @DisplayName("빈 목록을 저장할 때")
        class Context_빈_목록_저장 {

            @Test
            @DisplayName("아무것도 저장되지 않는다")
            void 아무것도_저장되지_않는다() {
                // given
                List<ArtistCreateRequest> requests = List.of();

                given(artistRepository.findAllById(anyList())).willReturn(List.of());

                // when
                artistService.saveIfNotExist(requests);

                // then
                verify(artistRepository, times(1)).findAllById(anyList());
                verify(artistRepository, times(1)).saveAll(argThat(artists -> {
                    List<Artist> artistList = (List<Artist>) artists;
                    return artistList.isEmpty();
                }));
            }
        }
    }

    @Nested
    @DisplayName("아티스트 조회")
    class Describe_아티스트_조회 {

        @Nested
        @DisplayName("존재하는 아티스트 ID로 조회할 때")
        class Context_존재하는_아티스트_조회 {

            @Test
            @DisplayName("아티스트가 성공적으로 조회된다")
            void 아티스트가_조회된다() {
                // given
                String artistId = "artist-1";
                Artist artist = ArtistFixture.createArtist(artistId, "하현상");

                given(artistRepository.findById(artistId)).willReturn(Optional.of(artist));

                // when
                Artist result = artistService.getArtistById(artistId);

                // then
                assertSoftly(softly -> {
                    softly.assertThat(result).isNotNull();
                    softly.assertThat(result.getId()).isEqualTo(artistId);
                    softly.assertThat(result.getName()).isEqualTo("하현상");
                });
                verify(artistRepository, times(1)).findById(artistId);
            }
        }

        @Nested
        @DisplayName("존재하지 않는 아티스트 ID로 조회할 때")
        class Context_존재하지_않는_아티스트_조회 {

            @Test
            @DisplayName("예외가 발생한다")
            void 예외가_발생한다() {
                // given
                String artistId = "non-existent-artist";

                given(artistRepository.findById(artistId)).willReturn(Optional.empty());

                // when & then
                assertThatThrownBy(() -> artistService.getArtistById(artistId))
                        .isInstanceOf(CustomException.class)
                        .hasMessageContaining(ArtistErrorCode.ARTIST_NOT_FOUND.getMessage());
                verify(artistRepository, times(1)).findById(artistId);
            }
        }
    }
}
