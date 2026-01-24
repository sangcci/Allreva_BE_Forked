package com.backend.allreva.module.concert.hall.infra;

import com.backend.allreva.module.concert.hall.application.dto.ConcertHallDetailResponse;

public interface ConcertHallRepositoryCustom {
    ConcertHallDetailResponse findDetailByHallCode(String hallCode);
}
