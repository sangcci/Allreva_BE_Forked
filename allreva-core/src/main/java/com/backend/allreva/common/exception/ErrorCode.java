package com.backend.allreva.common.exception;

public interface ErrorCode {

    int getStatus();

    String getCode();

    String getMessage();
}
