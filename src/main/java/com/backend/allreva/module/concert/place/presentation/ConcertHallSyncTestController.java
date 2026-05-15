package com.backend.allreva.module.concert.place.presentation;

import com.backend.allreva.module.concert.place.application.ConcertHallSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("!prod")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test/sync/halls")
public class ConcertHallSyncTestController implements ConcertHallSyncTestControllerSwagger {

    private final ConcertHallSyncService concertHallSyncService;

    @PostMapping
    public String syncHalls() {
        concertHallSyncService.fetchConcertHallInfoList();
        return "concert hall sync triggered";
    }
}
