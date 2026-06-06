package com.backend.allreva.concert.place.kopis;

import com.backend.allreva.common.config.KopisFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "kopisHallClient", url = "${public-data.kopis.base-url}", configuration = KopisFeignConfig.class)
public interface KopisHallClient {

    @GetMapping("/prfplc/{facilityCode}")
    KopisHallDetailResponse fetchFacility(@PathVariable(value = "facilityCode") String facilityCode);
}
