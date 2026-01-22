package com.backend.allreva.concert.infra.kopis;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.backend.allreva.concert.infra.dto.KopisConcertCodeResponse;
import com.backend.allreva.concert.infra.dto.KopisConcertResponse;

/**
 * Kopis(공연예술통합전산망) Concert API 클라이언트
 *
 * 공연 정보를 조회하는 외부 API 호출 인터페이스
 */
@FeignClient(name = "kopisConcertClient", url = "https://www.kopis.or.kr/openApi/restful", configuration = KopisClientConfig.class)
public interface KopisConcertClient {

    /**
     * 공연장별 공연 목록 조회
     *
     * @param hallCode  공연장 코드
     * @param startDate 조회 시작일 (yyyyMMdd)
     * @param endDate   조회 종료일 (yyyyMMdd)
     * @param today     오늘 이후 공연만 조회 (선택)
     * @return 공연 코드 목록
     */
    @GetMapping("${public-data.kopis.prfplc-url}")
    KopisConcertCodeResponse fetchConcertCodes(
            @RequestParam(value = "prfplccd") String hallCode,
            @RequestParam(value = "stdate") String startDate,
            @RequestParam(value = "eddate") String endDate,
            @RequestParam(value = "afterDate", required = false) String today);

    /**
     * 공연 상세 정보 조회
     *
     * @param concertCode 공연 코드
     * @return 공연 상세 정보
     */
    @GetMapping("${public-data.kopis.prf-url}")
    KopisConcertResponse fetchConcertDetail(
            @PathVariable(value = "concertCode") String concertCode);
}
