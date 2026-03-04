package com.backend.allreva.module.concert.artist.presentation;

import com.backend.allreva.common.web.response.Response;
import com.backend.allreva.module.concert.artist.application.dto.ArtistSearchResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

@Tag(name = "아티스트 API", description = "아티스트 API")
public interface ArtistControllerSwagger {

    @Operation(summary = "아티스트 검색", description = "아티스트 이름으로 검색")
    Response<List<ArtistSearchResponse>> searchArtist(String query);
}
