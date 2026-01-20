package com.backend.allreva.hall.ui;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.allreva.common.web.response.Response;
import com.backend.allreva.hall.command.application.AdminHallService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/admin/halls")
@RequiredArgsConstructor
public class AdminHallController {
    private final AdminHallService adminHallService;

    @PostMapping
    public Response<Void> fetchConcertHallInfoList() {
        adminHallService.fetchConcertHallInfoList();
        return Response.onSuccess();
    }
}
