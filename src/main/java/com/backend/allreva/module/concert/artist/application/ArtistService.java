package com.backend.allreva.module.concert.artist.application;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.module.concert.artist.application.dto.ArtistCreateRequest;
import com.backend.allreva.module.concert.artist.domain.Artist;
import com.backend.allreva.module.concert.artist.domain.ArtistRepository;
import com.backend.allreva.module.concert.artist.exception.ArtistErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArtistService {

    private final ArtistRepository artistRepository;

    @Transactional
    public void saveIfNotExist(final List<ArtistCreateRequest> artists) {
        List<String> ids = artists.stream()
                .map(ArtistCreateRequest::artistId)
                .toList();

        List<Artist> existingEntities = artistRepository.findAllById(ids);

        Set<String> existingIds = existingEntities.stream()
                .map(Artist::getId)
                .collect(Collectors.toSet());

        List<Artist> newArtists = artists.stream()
                .filter(artist -> !existingIds.contains(artist.artistId()))
                .map(req -> Artist.builder()
                        .id(req.artistId())
                        .name(req.name())
                        .build())
                .toList();

        artistRepository.saveAll(newArtists);
    }

    public Artist getArtistById(final String id) {
        return artistRepository.findById(id)
                .orElseThrow(() -> new CustomException(ArtistErrorCode.ARTIST_NOT_FOUND));
    }
}
