package com.backend.allreva.module.concert.place.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "[테스트] 공연장 동기화 API", description = "테스트용 KOPIS 공연장 정보 동기화 수동 트리거 API")
public interface ConcertHallSyncTestControllerSwagger {

    @Operation(summary = "공연장 정보 동기화 트리거", description = "KOPIS에서 공연장 정보를 수동으로 동기화합니다.")
    String syncHalls();
}
