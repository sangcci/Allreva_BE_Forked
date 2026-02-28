package com.backend.allreva.module.concert.place.infra.kopis;

import com.backend.allreva.common.config.OpenFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/** Kopis(공연예술통합전산망) Hall API 클라이언트 공연장 정보를 조회하는 외부 API 호출 인터페이스 */
@FeignClient(
        name = "kopisHallClient",
        url = "https://www.kopis.or.kr/openApi/restful",
        configuration = OpenFeignConfig.ClientXmlDecoder.class)
public interface KopisHallClient {

    /**
     * 공연장 상세 정보 조회
     *
     * @param hallCode 공연장 코드
     * @return 공연장 상세 정보
     */
    @GetMapping("${public-data.kopis.prfplc-detail-url}")
    KopisHallResponse fetchConcertHallDetail(@PathVariable(value = "hallCode") String hallCode);
}
