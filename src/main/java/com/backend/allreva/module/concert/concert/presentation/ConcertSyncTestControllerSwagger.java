package com.backend.allreva.module.concert.concert.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;

@Tag(name = "[테스트] 공연 동기화 API", description = "테스트용 KOPIS 공연 정보 동기화 수동 트리거 API")
public interface ConcertSyncTestControllerSwagger {

    @Operation(summary = "공연 정보 동기화 트리거", description = "KOPIS에서 공연 정보를 수동으로 동기화합니다. date 미입력 시 오늘 날짜 기준으로 실행됩니다.")
    String syncConcerts(LocalDate date);
}
