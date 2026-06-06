package com.backend.allreva.recruitment.rent;

import com.backend.allreva.auth.security.AuthMember;
import com.backend.allreva.common.pagination.SliceResponse;
import com.backend.allreva.common.web.response.View;
import com.backend.allreva.member.domain.Member;
import com.backend.allreva.recruitment.rent.domain.SortType;
import com.backend.allreva.recruitment.rent.query.application.RentFinder;
import com.backend.allreva.recruitment.rent.query.model.HostedRentSummaryResult;
import com.backend.allreva.recruitment.rent.query.model.JoinedRentResult;
import com.backend.allreva.recruitment.rent.query.model.RentDetailResult;
import com.backend.allreva.recruitment.rent.query.model.RentParticipantResult;
import com.backend.allreva.recruitment.rent.query.model.RentSummaryResult;
import com.backend.allreva.recruitment.rent.query.model.RentThumbnailResult;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rents")
public class RentQueryController implements RentQueryControllerSwagger {

    private final RentFinder rentQueryService;

    @Override
    @GetMapping("/suggestions")
    public View<List<RentThumbnailResult>> getRentSuggestions(@RequestParam final String query) {
        return View.onSuccess(rentQueryService.getRentSuggestions(query));
    }

    @Override
    @GetMapping("/search")
    public View<SliceResponse<RentThumbnailResult, Long>> searchRents(
            @RequestParam final String query,
            @RequestParam(defaultValue = "7") final int pageSize,
            @RequestParam(required = false) final Long cursorId) {
        return View.onSuccess(rentQueryService.searchRents(query, cursorId, pageSize));
    }

    @Override
    @GetMapping("/list")
    public View<List<RentSummaryResult>> getRentSummaries(
            @RequestParam(name = "region", required = false) final String region,
            @RequestParam(name = "sort", defaultValue = "LATEST") final SortType sortType,
            @RequestParam(name = "lastId", required = false) final Long lastId,
            @RequestParam(name = "lastEndDate", required = false) final LocalDate lastEndDate,
            @RequestParam(name = "pageSize", defaultValue = "10") final int pageSize) {
        return View.onSuccess(rentQueryService.getRentSummaries(region, sortType, lastEndDate, lastId, pageSize));
    }

    @Override
    @GetMapping("/{id}")
    public View<RentDetailResult> getRentDetail(@PathVariable final Long id) {
        return View.onSuccess(rentQueryService.getRentDetail(id));
    }

    @Override
    @GetMapping("/me/hosted")
    public View<List<HostedRentSummaryResult>> getRentHostedRentSummaries(
            @AuthMember final Member member,
            @RequestParam(name = "lastId", required = false) final Long lastId,
            @RequestParam(name = "pageSize", defaultValue = "10") final int pageSize) {
        return View.onSuccess(rentQueryService.getRentHostSummaries(member.getId(), lastId, pageSize));
    }

    @Override
    @GetMapping("/me/hosted/{id}")
    public View<List<RentParticipantResult>> getHostedRentDetail(
            @PathVariable("id") final Long rentId,
            @RequestParam final LocalDate boardingDate,
            @AuthMember final Member member) {
        return View.onSuccess(rentQueryService.getRentHostDetail(member.getId(), boardingDate, rentId));
    }

    @Override
    @GetMapping("/me/joined")
    public View<List<JoinedRentResult>> getJoinedRentSummaries(
            @AuthMember final Member member,
            @RequestParam(name = "lastId", required = false) final Long lastId,
            @RequestParam(name = "pageSize", defaultValue = "10") final int pageSize) {
        return View.onSuccess(rentQueryService.getJoinedRentSummaries(member.getId(), lastId, pageSize));
    }

    @Override
    @GetMapping("/me/joined/{id}")
    public View<RentParticipantResult> getJoinedRentDetail(
            @PathVariable("id") final Long rentId,
            @RequestParam final LocalDate boardingDate,
            @AuthMember final Member member) {
        return View.onSuccess(rentQueryService.getJoinedRentDetail(member.getId(), boardingDate, rentId));
    }
}
