package com.backend.allreva.module.concert.place.presentation;

import com.backend.allreva.common.web.response.Response;
import com.backend.allreva.module.concert.place.application.HallService;
import com.backend.allreva.module.concert.place.application.dto.ConcertHallDetailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/concert-halls")
@RestController
public class ConcertHallController implements ConcertHallControllerSwagger {

    private final HallService hallService;

    @Override
    public Response<ConcertHallDetailResponse> findHallDetailByHallCode(final String hallCode) {
        ConcertHallDetailResponse details = hallService.findDetailByHallCode(hallCode);
        return Response.onSuccess(details);
    }
}
