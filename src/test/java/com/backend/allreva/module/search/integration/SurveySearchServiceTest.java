package com.backend.allreva.module.search.integration;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.module.search.application.SurveySearchService;
import com.backend.allreva.support.IntegrationTestSupport;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@SuppressWarnings("NonAsciiCharacters")
@DisplayName("Survey 검색 통합 테스트")
class SurveySearchServiceTest extends IntegrationTestSupport {

    @Autowired
    SurveySearchService surveySearchService;

    @Nested
    @DisplayName("수요조사 썸네일 검색")
    class Describe_수요조사_썸네일_검색 {

        @Nested
        @DisplayName("검색어로 수요조사를 조회할 때")
        class Context_검색어로_조회 {

            @Test
            @DisplayName("빈 검색어인 경우 예외가 발생한다")
            void 빈_검색어인_경우_예외가_발생한다() {
                // when & then
                assertThrows(CustomException.class, () -> {
                    surveySearchService.searchSurveyThumbnails("");
                });
            }
        }
    }

    @Nested
    @DisplayName("수요조사 검색 리스트")
    class Describe_수요조사_검색_리스트 {

        @Nested
        @DisplayName("페이지네이션을 사용한 검색 조회 시")
        class Context_페이지네이션_검색_조회 {

            @Test
            @DisplayName("검색 결과가 없는 경우 예외가 발생한다")
            void 검색_결과가_없는_경우_예외가_발생한다() {
                // given
                String query = "존재하지않는검색어12345";

                // when & then
                assertThrows(CustomException.class, () -> {
                    surveySearchService.searchSurveyList(query, null, 2);
                });
            }
        }
    }
}
