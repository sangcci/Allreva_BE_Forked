package com.backend.allreva.recruitment.survey;

import com.backend.allreva.recruitment.survey.command.input.OpenSurveyCommand;
import com.backend.allreva.recruitment.survey.domain.Region;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public record OpenSurveyRequest(
        @NotBlank String title,
        @NotBlank String concertCode,
        @NotEmpty(message = "날짜는 하루 이상 선택되어야 합니다.") List<LocalDate> boardingDates,
        @NotNull Region region,
        @FutureOrPresent(message = "마감 기한은 과거일 수 없습니다.") LocalDate endDate,

        @NotNull @Min(value = 1, message = "탑승 인원 수는 1명 이상이어야 합니다.")
        Integer maxPassenger,

        String information) {

    public OpenSurveyCommand toCommand() {
        return new OpenSurveyCommand(title, concertCode, boardingDates, region, endDate, maxPassenger, information);
    }
}
