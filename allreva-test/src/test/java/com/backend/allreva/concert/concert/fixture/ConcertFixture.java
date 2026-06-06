package com.backend.allreva.concert.concert.fixture;

import static org.instancio.Select.field;

import com.backend.allreva.concert.concert.domain.Concert;
import com.backend.allreva.concert.concert.domain.ConcertInfo;
import com.backend.allreva.concert.concert.domain.ConcertStatus;
import com.backend.allreva.concert.concert.domain.DateInfo;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.instancio.Instancio;
import org.instancio.Model;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConcertFixture {

    public static Model<Concert> inProgressConcertModel() {
        return Instancio.of(Concert.class)
                .set(field(ConcertInfo.class, "performStatus"), ConcertStatus.IN_PROGRESS)
                .set(field(DateInfo.class, "startDate"), LocalDate.now().minusDays(10))
                .set(field(DateInfo.class, "endDate"), LocalDate.now().plusDays(10))
                .toModel();
    }

    public static Model<Concert> completedConcertModel() {
        return Instancio.of(Concert.class)
                .set(field(ConcertInfo.class, "performStatus"), ConcertStatus.COMPLETED)
                .set(field(DateInfo.class, "startDate"), LocalDate.of(2025, 1, 1))
                .set(field(DateInfo.class, "endDate"), LocalDate.of(2025, 3, 1))
                .toModel();
    }

    public static Model<Concert> scheduledConcertModel() {
        return Instancio.of(Concert.class)
                .set(field(ConcertInfo.class, "performStatus"), ConcertStatus.SCHEDULED)
                .set(field(DateInfo.class, "startDate"), LocalDate.now().plusDays(30))
                .set(field(DateInfo.class, "endDate"), LocalDate.now().plusDays(60))
                .toModel();
    }
}
