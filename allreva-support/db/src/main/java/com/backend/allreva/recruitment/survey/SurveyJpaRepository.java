package com.backend.allreva.recruitment.survey;

import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SurveyJpaRepository extends JpaRepository<SurveyEntity, Long> {

    @Modifying
    @Query("UPDATE Survey s SET s.isClosed = true WHERE s.endDate < :today")
    void closeSurveys(@Param("today") LocalDate today);
}
