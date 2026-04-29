package com.backend.allreva.module.recruitment.rent.presentation;

import com.backend.allreva.common.web.response.Response;
import com.backend.allreva.module.auth.security.AuthMember;
import com.backend.allreva.module.member.domain.Member;
import com.backend.allreva.module.recruitment.rent.application.RentService;
import com.backend.allreva.module.recruitment.rent.application.dto.HostedRentSummaryResponse;
import com.backend.allreva.module.recruitment.rent.application.dto.JoinedRentResponse;
import com.backend.allreva.module.recruitment.rent.application.dto.RentDetailResponse;
import com.backend.allreva.module.recruitment.rent.application.dto.RentIdRequest;
import com.backend.allreva.module.recruitment.rent.application.dto.RentJoinIdRequest;
import com.backend.allreva.module.recruitment.rent.application.dto.RentJoinRequest;
import com.backend.allreva.module.recruitment.rent.application.dto.RentJoinUpdateRequest;
import com.backend.allreva.module.recruitment.rent.application.dto.RentParticipantResponse;
import com.backend.allreva.module.recruitment.rent.application.dto.RentRegisterRequest;
import com.backend.allreva.module.recruitment.rent.application.dto.RentSummaryResponse;
import com.backend.allreva.module.recruitment.rent.application.dto.RentUpdateRequest;
import com.backend.allreva.module.recruitment.rent.application.dto.SortType;
import jakarta.validation.Valid;
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
public class RentController implements RentControllerSwagger {

    private final RentService rentService;

    // Anonymous EndPoints
    @Override
    @GetMapping("/list")
    public Response<List<RentSummaryResponse>> getRentSummaries(
            @RequestParam(name = "region", required = false) final String region,
            @RequestParam(name = "sort", defaultValue = "LATEST") final SortType sortType,
            @RequestParam(name = "lastId", required = false) final Long lastId,
            @RequestParam(name = "lastEndDate", required = false) final LocalDate lastEndDate,
            @RequestParam(name = "pageSize", defaultValue = "10") final int pageSize) {
        return Response.onSuccess(rentService.getRentSummaries(region, sortType, lastEndDate, lastId, pageSize));
    }

    @Override
    @GetMapping("/{id}")
    public Response<RentDetailResponse> getRentDetail(@PathVariable final Long id) {
        return Response.onSuccess(rentService.getRentDetail(id));
    }

    // Host Endpoints
    @Override
    @PostMapping
    public Response<Long> registerRent(
            @RequestBody @Valid final RentRegisterRequest rentRegisterRequest, @AuthMember final Member member) {
        Long rentId = rentService.registerRent(rentRegisterRequest, member.getId());
        return Response.onSuccess(rentId);
    }

    @Override
    @PatchMapping
    public Response<Void> updateRent(
            @RequestBody @Valid final RentUpdateRequest rentUpdateRequest, @AuthMember final Member member) {
        rentService.updateRent(rentUpdateRequest, member.getId());
        return Response.onSuccess();
    }

    @Override
    @PatchMapping("/close")
    public Response<Void> closeRent(
            @RequestBody @Valid final RentIdRequest rentIdRequest, @AuthMember final Member member) {
        rentService.closeRent(rentIdRequest, member.getId());
        return Response.onSuccess();
    }

    @Override
    @DeleteMapping
    public Response<Void> deleteRent(
            @RequestBody @Valid final RentIdRequest rentIdRequest, @AuthMember final Member member) {
        rentService.deleteRent(rentIdRequest, member.getId());
        return Response.onSuccess();
    }

    @Override
    @GetMapping("/me/hosted")
    public Response<List<HostedRentSummaryResponse>> getRentHostedRentSummaries(
            @AuthMember Member member,
            @RequestParam(name = "lastId", required = false) final Long lastId,
            @RequestParam(name = "pageSize", defaultValue = "10") final int pageSize) {
        return Response.onSuccess(rentService.getRentHostSummaries(member.getId(), lastId, pageSize));
    }

    @Override
    @GetMapping("/me/hosted/{id}")
    public Response<List<RentParticipantResponse>> getHostedRentDetail(
            @PathVariable("id") final Long rentId,
            @RequestParam final LocalDate boardingDate,
            @AuthMember Member member) {
        return Response.onSuccess(rentService.getRentHostDetail(member.getId(), boardingDate, rentId));
    }

    // Participant Endpoints
    @Override
    @PostMapping("/join")
    public Response<Long> joinRent(
            @RequestBody @Valid final RentJoinRequest rentJoinRequest, @AuthMember final Member member) {
        Long participantId = rentService.joinRent(rentJoinRequest, member.getId());
        return Response.onSuccess(participantId);
    }

    @Override
    @PatchMapping("/join")
    public Response<Void> updateRentJoin(
            @RequestBody @Valid final RentJoinUpdateRequest rentJoinUpdateRequest, @AuthMember final Member member) {
        rentService.updateRentJoin(rentJoinUpdateRequest, member.getId());
        return Response.onSuccess();
    }

    @Override
    @DeleteMapping("/join")
    public Response<Void> cancelRentJoin(
            @RequestBody @Valid final RentJoinIdRequest rentJoinIdRequest, @AuthMember final Member member) {
        rentService.cancelRentJoin(rentJoinIdRequest, member.getId());
        return Response.onSuccess();
    }

    @Override
    @GetMapping("/me/joined")
    public Response<List<JoinedRentResponse>> getJoinedRentSummeries(
            @AuthMember final Member member,
            @RequestParam(name = "lastId", required = false) final Long lastId,
            @RequestParam(name = "pageSize", defaultValue = "10") final int pageSize) {
        return Response.onSuccess(rentService.getJoinedRentSummaries(member.getId(), lastId, pageSize));
    }

    @Override
    @GetMapping("/me/joined/{id}")
    public Response<RentParticipantResponse> getJoinedRentDetail(
            @PathVariable("id") final Long rentId,
            @RequestParam final LocalDate boardingDate,
            @AuthMember Member member) {
        return Response.onSuccess(rentService.getJoinedRentDetail(member.getId(), boardingDate, rentId));
    }
}
