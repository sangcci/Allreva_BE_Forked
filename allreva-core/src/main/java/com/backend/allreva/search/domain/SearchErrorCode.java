package com.backend.allreva.search.domain;

import com.backend.allreva.common.exception.ErrorCode;

public enum SearchErrorCode implements ErrorCode {
    ELASTICSEARCH_ERROR(500, "ELASTICSEARCH_ERROR", "elasticsearch 서버 오류입니다."),
    SEARCH_RESULT_NOT_FOUND(404, "SEARCH_RESULT_NOT_FOUND", "검색어에 매칭 되는 결과가 없습니다."),
    EVENT_PUBLISHING_FAIL(500, "EVENT_PUBLISHING_FAIL", "이벤트 발행 실패"),
    EVENT_CONSUMING_FAIL(500, "EVENT_CONSUMING_FAIL", "이벤트 소비 실패");

    private final int status;
    private final String code;
    private final String message;

    SearchErrorCode(final int status, final String code, final String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
