package com.backend.allreva.recruitment.survey.query.model;

import com.backend.allreva.recruitment.survey.domain.Region;
import java.time.LocalDate;

public record SurveyMain(Long id, String title, Region region, Integer participationCount, LocalDate edDate) {}
