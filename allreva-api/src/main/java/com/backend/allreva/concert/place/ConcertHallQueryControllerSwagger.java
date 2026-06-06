package com.backend.allreva.concert.place;

import com.backend.allreva.common.web.response.View;
import com.backend.allreva.concert.place.query.model.ConcertHallDetailResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "공연장 API", description = "공연장 Query API")
public interface ConcertHallQueryControllerSwagger {

    @Operation(summary = "공연장 상세 조회")
    View<ConcertHallDetailResult> getConcertHallDetail(@NotBlank @PathVariable("hallCode") String hallCode);
}
