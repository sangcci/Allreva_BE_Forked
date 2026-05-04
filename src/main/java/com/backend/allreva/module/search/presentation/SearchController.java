package com.backend.allreva.module.search.presentation;

import com.backend.allreva.common.web.response.Response;
import com.backend.allreva.module.search.application.PopularKeywordService;
import com.backend.allreva.module.search.application.dto.PopularKeywordResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/search")
public class SearchController implements SearchControllerSwagger {

    private final PopularKeywordService popularKeywordService;

    @Override
    @GetMapping("/popular")
    public Response<List<PopularKeywordResponse>> getPopularKeywordRank() {
        return Response.onSuccess(popularKeywordService.getPopularKeywordRank());
    }
}
