package com.backend.allreva.module.artist.exception;

import org.springframework.http.HttpStatus;

import com.backend.allreva.common.exception.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ArtistErrorCode implements ErrorCode {
    ARTIST_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "ARTIST_NOT_FOUND", "존재하지 않는 아티스트 입니다."),
    ARTIST_SEARCH_NO_CONTENT(HttpStatus.NO_CONTENT.value(), "ARTIST_SEARCH_NO_CONTENT", "아티스트 검색 결과가 없습니다.");

    private final int status;
    private final String code;
    private final String message;

}
