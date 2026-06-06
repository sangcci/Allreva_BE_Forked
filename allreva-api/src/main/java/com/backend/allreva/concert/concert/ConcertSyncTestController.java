package com.backend.allreva.concert.concert;

import com.backend.allreva.concert.concert.command.application.ConcertSyncService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Profile("!prod")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test/sync/concerts")
public class ConcertSyncTestController implements ConcertSyncTestControllerSwagger {

    private final ConcertSyncService concertSyncService;

    @PostMapping
    public String syncConcerts(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate target = date != null ? date : LocalDate.now();
        concertSyncService.sync(target);
        return "concert sync triggered for " + target;
    }
}
