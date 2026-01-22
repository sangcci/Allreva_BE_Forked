package com.backend.allreva.module.diary.domain;

import com.backend.allreva.module.diary.infra.DiaryDslRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiaryRepository extends JpaRepository<ConcertDiary, Long>, DiaryDslRepository {
}
