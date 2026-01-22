package com.backend.allreva.hall.infra.kopis;

import com.backend.allreva.hall.command.application.KopisHallService;
import com.backend.allreva.hall.infra.dto.KopisHallResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Kopis Hall API 어댑터
 *
 * KopisHallClient를 사용하여 공연장 정보를 조회합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KopisHallAdapter implements KopisHallService {
    private final KopisHallClient kopisHallClient;

    @Override
    public KopisHallResponse fetchConcertHallInfoList(String hallCode) {
        return kopisHallClient.fetchConcertHallDetail(hallCode);
    }
}
