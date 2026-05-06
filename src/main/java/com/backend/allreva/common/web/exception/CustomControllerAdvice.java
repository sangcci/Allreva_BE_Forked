package com.backend.allreva.common.web.exception;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.common.exception.ErrorCode;
import com.backend.allreva.common.exception.GlobalErrorCode;
import com.backend.allreva.common.web.response.Response;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class CustomControllerAdvice {

    @ExceptionHandler(value = CustomException.class)
    public ResponseEntity<?> handleCustomException(CustomException e) {
        ErrorCode errorCode = e.getErrorCode();

        return ResponseEntity.status(errorCode.getStatus())
                .body(Response.onFailure(errorCode.getCode(), errorCode.getMessage()));
    }

    // @RequestBody bean validation failure
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.info("request body validation failed: {}", e.getMessage());
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

    // @RequestParam type conversion failure (e.g. invalid enum value)
    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.info("request param type mismatch: param={}, value={}", e.getName(), e.getValue());
        String message = String.format("'%s' 파라미터에 유효하지 않은 값입니다: %s", e.getName(), e.getValue());

        return ResponseEntity.status(GlobalErrorCode.BAD_REQUEST_ERROR.getStatus())
                .body(Response.onFailure(GlobalErrorCode.BAD_REQUEST_ERROR.getCode(), message));
    }

    // JSON body parse failure (malformed JSON, invalid enum in body, wrong type)
    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.info("request body not readable: {}", e.getMessage());

        return ResponseEntity.status(GlobalErrorCode.BAD_REQUEST_ERROR.getStatus())
                .body(Response.onFailure(GlobalErrorCode.BAD_REQUEST_ERROR.getCode(), "JSON 형식에 맞지 않는 요청입니다."));
    }

    // required @RequestParam missing
    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public ResponseEntity<?> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.info("required request param missing: param={}", e.getParameterName());
        String message = String.format("required parameter '%s' is missing", e.getParameterName());

        return ResponseEntity.status(GlobalErrorCode.BAD_REQUEST_ERROR.getStatus())
                .body(Response.onFailure(GlobalErrorCode.BAD_REQUEST_ERROR.getCode(), message));
    }

    // @ModelAttribute binding failure
    @ExceptionHandler(value = BindException.class)
    public ResponseEntity<?> handleBindException(BindException e) {
        log.info("model attribute binding failed: {}", e.getMessage());
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

    // @Validated method parameter constraint violation
    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException e) {
        log.info("constraint violation: {}", e.getMessage());
        StringBuilder errorMessage = new StringBuilder();

        e.getConstraintViolations().forEach(v -> errorMessage
                .append(v.getPropertyPath())
                .append(": ")
                .append(v.getMessage())
                .append("\n"));

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
