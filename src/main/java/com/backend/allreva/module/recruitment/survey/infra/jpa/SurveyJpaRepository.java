package com.backend.allreva.module.recruitment.survey.infra.jpa;

import com.backend.allreva.module.recruitment.survey.domain.Survey;
import io.lettuce.core.dynamic.annotation.Param;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface SurveyJpaRepository extends JpaRepository<Survey, Long> {

    @Modifying
    @Query("UPDATE Survey s SET s.isClosed = true WHERE s.endDate < :today")
    void closeSurveys(@Param("today") LocalDate today);
}
