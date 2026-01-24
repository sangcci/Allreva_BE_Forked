package com.backend.allreva.module.concert.hall.presentation;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.allreva.common.web.response.Response;
import com.backend.allreva.module.concert.hall.application.HallService;
import com.backend.allreva.module.concert.hall.application.dto.ConcertHallDetailResponse;
import com.backend.allreva.module.concert.hall.application.dto.RelatedConcertResponse;

import lombok.RequiredArgsConstructor;

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

    @Override
    public Response<List<RelatedConcertResponse>> findRelatedConcertList(
            final String hallCode,
            final Long lastId,
            final Long lastViewCount,
            final int pageSize) {
        return Response.onSuccess(
                hallService.getRelatedConcert(hallCode, lastId, lastViewCount, pageSize));
    }
}
