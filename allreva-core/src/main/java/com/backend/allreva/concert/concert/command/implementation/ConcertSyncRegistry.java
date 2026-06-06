package com.backend.allreva.concert.concert.command.implementation;

import com.backend.allreva.concert.place.command.implementation.ConcertHallRegistryPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConcertSyncRegistry {

    private final ConcertHallRegistryPort concertHallRegistryPort;

    public List<String> hallCodes() {
        return concertHallRegistryPort.findAllHallCodes();
    }
}
