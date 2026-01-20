package com.backend.allreva.keyword.ui;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.allreva.common.web.response.Response;
import com.backend.allreva.keyword.query.application.PopularKeywordQueryService;
import com.backend.allreva.keyword.query.application.dto.PopularKeywordResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/search")
public class PopularKeywordController {

    private final PopularKeywordQueryService popularKeywordService;

    @GetMapping("/popular")
    public Response<List<PopularKeywordResponse>> getPopularKeywordRank() {
        return Response.onSuccess(
                popularKeywordService.getPopularKeywordRank());
    }
}
