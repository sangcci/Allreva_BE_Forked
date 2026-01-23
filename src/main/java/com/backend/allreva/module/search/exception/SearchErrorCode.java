package com.backend.allreva.module.search.exception;

import org.springframework.http.HttpStatus;

import com.backend.allreva.common.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SearchErrorCode implements ErrorCode {
    ELASTICSEARCH_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "ELASTICSEARCH_ERROR", "elasticsearch 서버 오류입니다."),
    SEARCH_RESULT_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "SEARCH_RESULT_NOT_FOUND", "검색어에 매칭 되는 결과가 없습니다."),
    EVENT_PUBLISHING_FAIL(HttpStatus.INTERNAL_SERVER_ERROR.value(), "EVENT_PUBLISHING_FAIL", "이벤트 발행 실패"),
    EVENT_CONSUMING_FAIL(HttpStatus.INTERNAL_SERVER_ERROR.value(), "EVENT_CONSUMING_FAIL", "이벤트 소비 실패");

    private final int status;
    private final String code;
    private final String message;

}
