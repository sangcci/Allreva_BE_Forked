package com.backend.allreva.concert.place;

import com.backend.allreva.common.web.response.View;
import com.backend.allreva.concert.place.query.application.ConcertHallFinder;
import com.backend.allreva.concert.place.query.model.ConcertHallDetailResult;
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
public class ConcertHallQueryController implements ConcertHallQueryControllerSwagger {

    private final ConcertHallFinder concertHallQueryService;

    @Override
    @GetMapping("/{hallCode}")
    public View<ConcertHallDetailResult> getConcertHallDetail(@PathVariable("hallCode") final String hallCode) {
        return View.onSuccess(concertHallQueryService.getConcertHallDetail(hallCode));
    }
}
