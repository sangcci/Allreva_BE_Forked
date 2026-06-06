package com.backend.allreva.recruitment.rent;

import com.backend.allreva.auth.security.AuthMember;
import com.backend.allreva.common.web.response.View;
import com.backend.allreva.member.domain.Member;
import com.backend.allreva.recruitment.rent.command.application.RentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rents")
public class RentCommandController implements RentCommandControllerSwagger {

    private final RentService rentCommandService;

    @Override
    @PostMapping
    public View<Long> registerRent(
            @Valid @RequestBody final RentRegisterRequest request, @AuthMember final Member member) {
        return View.onSuccess(rentCommandService.register(request.toCommand(), member.getId()));
    }

    @Override
    @PatchMapping
    public View<Void> updateRent(@Valid @RequestBody final RentUpdateRequest request, @AuthMember final Member member) {
        rentCommandService.update(request.toCommand(), member.getId());
        return View.onSuccess();
    }

    @Override
    @PatchMapping("/close")
    public View<Void> closeRent(@Valid @RequestBody final RentIdRequest request, @AuthMember final Member member) {
        rentCommandService.close(request.toCommand(), member.getId());
        return View.onSuccess();
    }

    @Override
    @DeleteMapping
    public View<Void> deleteRent(@Valid @RequestBody final RentIdRequest request, @AuthMember final Member member) {
        rentCommandService.delete(request.toCommand(), member.getId());
        return View.onSuccess();
    }

    @Override
    @PostMapping("/join")
    public View<Long> joinRent(@Valid @RequestBody final RentJoinRequest request, @AuthMember final Member member) {
        return View.onSuccess(rentCommandService.join(request.toCommand(), member.getId()));
    }

    @Override
    @PatchMapping("/join")
    public View<Void> updateRentJoin(
            @Valid @RequestBody final RentJoinUpdateRequest request, @AuthMember final Member member) {
        rentCommandService.updateJoin(request.toCommand(), member.getId());
        return View.onSuccess();
    }

    @Override
    @DeleteMapping("/join")
    public View<Void> cancelRentJoin(
            @Valid @RequestBody final RentJoinIdRequest request, @AuthMember final Member member) {
        rentCommandService.cancelJoin(request.toCommand(), member.getId());
        return View.onSuccess();
    }
}
