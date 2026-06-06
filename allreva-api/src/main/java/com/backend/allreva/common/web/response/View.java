package com.backend.allreva.common.web.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.time.LocalDateTime;

@JsonPropertyOrder({"timeStamp", "code", "message", "result"})
public class View<T> {

    private final LocalDateTime timeStamp = LocalDateTime.now();
    private final String code;
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T result;

    public View(String code, String message, T result) {
        this.code = code;
        this.message = message;
        this.result = result;
    }

    public View(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static <T> View<T> onSuccess(T result) {
        return new View<>("SUCCESS", "요청에 성공하였습니다.", result);
    }

    public static <T> View<T> onSuccess() {
        return new View<>("SUCCESS", "요청에 성공하였습니다.");
    }

    public static <T> View<T> onFailure(String errorCode, String message) {
        return new View<>(errorCode, message);
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getResult() {
        return result;
    }
}
