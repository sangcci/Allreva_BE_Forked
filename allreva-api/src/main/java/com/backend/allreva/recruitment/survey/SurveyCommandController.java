package com.backend.allreva.recruitment.survey;

import com.backend.allreva.auth.security.AuthMember;
import com.backend.allreva.common.web.response.View;
import com.backend.allreva.member.domain.Member;
import com.backend.allreva.recruitment.survey.command.application.SurveyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/surveys")
public class SurveyCommandController implements SurveyCommandControllerSwagger {

    private final SurveyService surveyCommandService;

    @Override
    @PostMapping
    public View<Long> openSurvey(@AuthMember final Member member, @Valid @RequestBody final OpenSurveyRequest request) {
        return View.onSuccess(surveyCommandService.open(request.toCommand(), member.getId()));
    }

    @Override
    @PatchMapping
    public View<Void> updateSurvey(
            @AuthMember final Member member, @Valid @RequestBody final UpdateSurveyRequest request) {
        surveyCommandService.update(request.toCommand(), member.getId());
        return View.onSuccess();
    }

    @Override
    @DeleteMapping
    public View<Void> removeSurvey(@AuthMember final Member member, @Valid @RequestBody final SurveyIdRequest request) {
        surveyCommandService.delete(request.toCommand(), member.getId());
        return View.onSuccess();
    }

    @Override
    @PostMapping("/apply")
    public View<Long> joinSurvey(@AuthMember final Member member, @Valid @RequestBody final JoinSurveyRequest request) {
        return View.onSuccess(surveyCommandService.join(request.toCommand(), member.getId()));
    }

    @Override
    @DeleteMapping("/apply/{participantId}")
    public View<Void> cancelJoin(
            @AuthMember final Member member, @PathVariable(name = "participantId") final Long participantId) {
        surveyCommandService.cancelJoin(participantId, member.getId());
        return View.onSuccess();
    }
}
