package com.backend.allreva.recruitment.survey.domain;

import com.backend.allreva.common.exception.ErrorCode;

public enum SurveyErrorCode implements ErrorCode {
    SURVEY_NOT_FOUND(404, "SURVEY_NOT_FOUND", "존재하지 않는 수요조사입니다."),
    SURVEY_PARTICIPANT_NOT_FOUND(404, "SURVEY_PARTICIPANT_NOT_FOUND", "존재하지 않는 수요조사 참여자입니다."),
    SURVEY_NOT_WRITER(403, "SURVEY_NOT_WRITER", "작성자가 아니므로 권한이 없습니다."),
    SURVEY_ILLEGAL_PARAMETER(400, "SURVEY_ILLEGAL_PARAMETER", "lastEndDate는 lastId와 함께 제공되어야합니다."),
    SURVEY_INVALID_BOARDING_DATE(400, "SURVEY_INVALID_BOARDING_DATE", "가용 날짜가 아닙니다."),
    SURVEY_EVENT_PUBLISHING_FAIL(500, "SURVEY_EVENT_PUBLISHING_FAIL", "survey event 발행 실패"),
    SURVEY_EVENT_CONSUMING_FAIL(500, "SURVEY_EVENT_CONSUMING_FAIL", "survey event 소비 실패"),
    SURVEY_JOIN_ALREADY_EXISTS(400, "SURVEY_JOIN_ALREADY_EXISTS", "이미 해당 수요조사에 참여했습니다."),
    SURVEY_JOIN_ACCESS_DENIED(403, "SURVEY_JOIN_ACCESS_DENIED", "수요조사 참여에 대한 접근 권한이 없습니다.");

    private final int status;
    private final String code;
    private final String message;

    SurveyErrorCode(final int status, final String code, final String message) {
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
