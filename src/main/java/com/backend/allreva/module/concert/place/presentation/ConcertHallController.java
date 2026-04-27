package com.backend.allreva.module.concert.place.presentation;

import com.backend.allreva.common.web.response.Response;
import com.backend.allreva.module.concert.place.application.ConcertHallService;
import com.backend.allreva.module.concert.place.application.dto.ConcertHallDetailResponse;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/concert-halls")
@RestController
@Validated
public class ConcertHallController implements ConcertHallControllerSwagger {

    private final ConcertHallService concertHallService;

    @Override
    @GetMapping("/{hallCode}")
    public Response<ConcertHallDetailResponse> getConcertHallDetail(
            @NotBlank @PathVariable("hallCode") final String hallCode) {
        return Response.onSuccess(concertHallService.getConcertHallDetail(hallCode));
    }
}
