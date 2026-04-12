package com.backend.allreva.common.web.exception;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.common.exception.ErrorCode;
import com.backend.allreva.common.exception.GlobalErrorCode;
import com.backend.allreva.common.web.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class CustomControllerAdvice {

    @ExceptionHandler(value = CustomException.class)
    public ResponseEntity<?> handleCustomException(CustomException e) {
        ErrorCode errorCode = e.getErrorCode();

        return ResponseEntity.status(errorCode.getStatus())
                .body(Response.onFailure(errorCode.getCode(), errorCode.getMessage()));
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.info("user validation error: {}", e.getMessage(), e);
        StringBuilder errorMessage = new StringBuilder();

        for (ObjectError error : e.getBindingResult().getAllErrors()) {
            String field = (error instanceof FieldError fe) ? fe.getField() : error.getObjectName();
            errorMessage
                    .append(field)
                    .append(": ")
                    .append(error.getDefaultMessage())
                    .append("\n");
        }

        return ResponseEntity.status(GlobalErrorCode.BAD_REQUEST_ERROR.getStatus())
                .body(Response.onFailure(GlobalErrorCode.BAD_REQUEST_ERROR.getCode(), errorMessage.toString()));
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        log.error("unexpected error: {}", e.getMessage(), e);
        return ResponseEntity.status(GlobalErrorCode.SERVER_ERROR.getStatus())
                .body(Response.onFailure(
                        GlobalErrorCode.SERVER_ERROR.getCode(), GlobalErrorCode.SERVER_ERROR.getMessage()));
    }
}
