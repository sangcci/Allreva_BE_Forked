package com.backend.allreva.module.diary.infra;

import com.backend.allreva.module.diary.application.dto.DiaryDetailResponse;
import com.backend.allreva.module.diary.application.dto.DiarySummaryResponse;

import java.util.List;

public interface DiaryDslRepository {
    DiaryDetailResponse findDetail(Long diaryId, Long memberId);

    List<DiarySummaryResponse> findSummaries(Long memberId, int year, int month);
}
