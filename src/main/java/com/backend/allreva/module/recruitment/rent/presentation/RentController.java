package com.backend.allreva.module.recruitment.rent.presentation;

import com.backend.allreva.common.web.response.Response;
import com.backend.allreva.module.auth.security.AuthMember;
import com.backend.allreva.module.member.domain.Member;
import com.backend.allreva.module.recruitment.rent.application.RentService;
import com.backend.allreva.module.recruitment.rent.application.dto.DepositAccountResponse;
import com.backend.allreva.module.recruitment.rent.application.dto.HostedRentDetailResponse;
import com.backend.allreva.module.recruitment.rent.application.dto.HostedRentSummaryResponse;
import com.backend.allreva.module.recruitment.rent.application.dto.JoinedRentDetailResponse;
import com.backend.allreva.module.recruitment.rent.application.dto.JoinedRentResponse;
import com.backend.allreva.module.recruitment.rent.application.dto.RentDetailResponse;
import com.backend.allreva.module.recruitment.rent.application.dto.RentIdRequest;
import com.backend.allreva.module.recruitment.rent.application.dto.RentJoinIdRequest;
import com.backend.allreva.module.recruitment.rent.application.dto.RentJoinRequest;
import com.backend.allreva.module.recruitment.rent.application.dto.RentJoinUpdateRequest;
import com.backend.allreva.module.recruitment.rent.application.dto.RentMeResponse;
import com.backend.allreva.module.recruitment.rent.application.dto.RentRegisterRequest;
import com.backend.allreva.module.recruitment.rent.application.dto.RentSummaryResponse;
import com.backend.allreva.module.recruitment.rent.application.dto.RentUpdateRequest;
import com.backend.allreva.module.recruitment.rent.application.dto.SortType;
import com.backend.allreva.module.recruitment.rent.domain.value.Region;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rents")
public class RentController {

    private final RentService rentService;

    // User EndPoints
    @GetMapping("/main")
    public Response<List<RentSummaryResponse>> getRentMainSummaries() {
        return Response.onSuccess(rentService.getRentSummaries(null, SortType.LATEST, null, null, 3));
    }

    @GetMapping("/list")
    public Response<List<RentSummaryResponse>> getRentSummaries(
            @RequestParam(name = "region", required = false) final Region region,
            @RequestParam(name = "sort", defaultValue = "LATEST") final SortType sortType,
            @RequestParam(name = "lastId", required = false) final Long lastId,
            @RequestParam(name = "lastEndDate", required = false) final LocalDate lastEndDate,
            @RequestParam(name = "pageSize", defaultValue = "10") @Min(10) final int pageSize) {
        return Response.onSuccess(rentService.getRentSummaries(region, sortType, lastEndDate, lastId, pageSize));
    }

    @GetMapping("/{id}")
    public Response<RentDetailResponse> getRentDetail(@PathVariable final Long id) {
        return Response.onSuccess(rentService.getRentDetail(id));
    }

    // User Endpoints
    @GetMapping("/{id}/me")
    public Response<RentMeResponse> getRentDetailMe(@PathVariable final Long id, @AuthMember final Member member) {
        return Response.onSuccess(rentService.getRentDetailMe(id, member));
    }

    // Host Endpoints
    @PostMapping
    public Response<Long> registerRent(
            @RequestBody @Valid final RentRegisterRequest rentRegisterRequest, @AuthMember final Member member) {
        Long rentId = rentService.registerRent(rentRegisterRequest, member.getId());
        return Response.onSuccess(rentId);
    }

    @PatchMapping
    public Response<Void> updateRent(
            @RequestBody @Valid final RentUpdateRequest rentUpdateRequest, @AuthMember final Member member) {
        rentService.updateRent(rentUpdateRequest, member.getId());
        return Response.onSuccess();
    }

    @PatchMapping("/close")
    public Response<Void> closeRent(
            @RequestBody @Valid final RentIdRequest rentIdRequest, @AuthMember final Member member) {
        rentService.closeRent(rentIdRequest, member.getId());
        return Response.onSuccess();
    }

    @DeleteMapping
    public Response<Void> deleteRent(
            @RequestBody @Valid final RentIdRequest rentIdRequest, @AuthMember final Member member) {
        rentService.deleteRent(rentIdRequest, member.getId());
        return Response.onSuccess();
    }

    @GetMapping("/host/me/list")
    public Response<List<HostedRentSummaryResponse>> getRentHostedRentSummaries(
            @AuthMember Member member,
            @RequestParam(name = "lastId", required = false) final Long lastId,
            @RequestParam(name = "pageSize", defaultValue = "10") @Min(10) final int pageSize) {
        return Response.onSuccess(rentService.getRentHostSummaries(member.getId(), lastId, pageSize));
    }

    @GetMapping("/{id}/deposit-account")
    public Response<DepositAccountResponse> getDepositAccountById(@PathVariable final Long id) {
        return Response.onSuccess(rentService.getDepositAccount(id));
    }

    @GetMapping("/host/me/{id}")
    public Response<HostedRentDetailResponse> getHostedRentDetail(
            @PathVariable("id") final Long rentId,
            @RequestParam final LocalDate boardingDate,
            @AuthMember Member member) {
        return Response.onSuccess(rentService.getRentHostDetail(member.getId(), boardingDate, rentId));
    }

    // Participant Endpoints
    @PostMapping("/join")
    public Response<Long> joinRent(
            @RequestBody @Valid final RentJoinRequest rentJoinRequest, @AuthMember final Member member) {
        Long participantId = rentService.joinRent(rentJoinRequest, member.getId());
        return Response.onSuccess(participantId);
    }

    @PatchMapping("/join")
    public Response<Void> updateRentJoin(
            @RequestBody @Valid final RentJoinUpdateRequest rentJoinUpdateRequest, @AuthMember final Member member) {
        rentService.updateRentJoin(rentJoinUpdateRequest, member.getId());
        return Response.onSuccess();
    }

    @DeleteMapping("/join")
    public Response<Void> cancelRentJoin(
            @RequestBody @Valid final RentJoinIdRequest rentJoinIdRequest, @AuthMember final Member member) {
        rentService.cancelRentJoin(rentJoinIdRequest, member.getId());
        return Response.onSuccess();
    }

    @GetMapping("/join/me/list")
    public Response<List<JoinedRentResponse>> getJoinedRentSummeries(@AuthMember final Member member) {
        return Response.onSuccess(rentService.getJoinedRentSummaries(member.getId()));
    }

    @GetMapping("/join/me/{id}")
    public Response<JoinedRentDetailResponse> getJoinedRentDetail(
            @PathVariable("id") final Long rentId,
            @RequestParam final LocalDate boardingDate,
            @AuthMember Member member) {
        return Response.onSuccess(rentService.getJoinedRentDetail(member.getId(), boardingDate, rentId));
    }
}
