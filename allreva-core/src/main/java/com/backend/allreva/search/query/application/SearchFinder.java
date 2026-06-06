package com.backend.allreva.search.query.application;

import com.backend.allreva.search.query.implementation.SearchFinderPort;
import com.backend.allreva.search.query.model.PopularKeywordResult;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchFinder {

    private final SearchFinderPort searchFinderPort;

    public List<PopularKeywordResult> getPopularKeywordRank() {
        return searchFinderPort.findPopularKeywordRank();
    }
}
