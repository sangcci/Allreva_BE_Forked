package com.backend.allreva.search;

import com.backend.allreva.common.web.response.View;
import com.backend.allreva.search.query.application.SearchFinder;
import com.backend.allreva.search.query.model.PopularKeywordResult;
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
public class SearchQueryController implements SearchQueryControllerSwagger {

    private final SearchFinder searchQueryService;

    @Override
    @GetMapping("/popular")
    public View<List<PopularKeywordResult>> getPopularKeywordRank() {
        return View.onSuccess(searchQueryService.getPopularKeywordRank());
    }
}
