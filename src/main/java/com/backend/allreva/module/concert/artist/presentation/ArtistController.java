package com.backend.allreva.module.concert.artist.presentation;

import com.backend.allreva.common.web.response.Response;
import com.backend.allreva.module.concert.artist.application.dto.ArtistSearchResponse;
import com.backend.allreva.module.concert.artist.application.port.ArtistSearchPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/artists")
@RequiredArgsConstructor
public class ArtistController implements ArtistControllerSwagger {
    private final ArtistSearchPort artistSearchPort;

    @GetMapping("/search")
    public Response<List<ArtistSearchResponse>> searchArtist(final @RequestParam String query) {
        return Response.onSuccess(artistSearchPort.searchArtist(query));
    }
}
