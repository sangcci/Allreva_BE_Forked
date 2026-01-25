package com.backend.allreva.module.concert.diary.domain;

import com.backend.allreva.module.concert.diary.infra.DiaryDslRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiaryRepository extends JpaRepository<ConcertDiary, Long>, DiaryDslRepository {
}
