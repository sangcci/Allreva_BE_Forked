package com.backend.allreva.module.concert.concert.infra.kopis;

import com.backend.allreva.common.config.KopisFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "kopisConcertClient", url = "${public-data.kopis.base-url}", configuration = KopisFeignConfig.class)
public interface KopisConcertClient {

    @GetMapping("/pblprfr?rows=100&cpage=1")
    KopisConcertCodeResponse fetchConcertCodes(
            @RequestParam(value = "prfplccd") String hallCode,
            @RequestParam(value = "stdate") String startDate,
            @RequestParam(value = "eddate") String endDate,
            @RequestParam(value = "afterDate", required = false) String today,
            @RequestParam(value = "shcate", required = false) String genreCode);

    @GetMapping("/pblprfr/{concertCode}")
    KopisConcertResponse fetchConcertDetail(@PathVariable(value = "concertCode") String concertCode);
}
