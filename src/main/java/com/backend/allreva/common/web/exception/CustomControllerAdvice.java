package com.backend.allreva.common.web.exception;

import com.backend.allreva.common.exception.CustomException;
import com.backend.allreva.common.exception.ErrorCode;
import com.backend.allreva.common.exception.GlobalErrorCode;
import com.backend.allreva.common.web.response.Response;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class CustomControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<?> handleCustomException(CustomException e) {
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity.status(errorCode.getStatus())
                .body(Response.onFailure(errorCode.getCode(), errorCode.getMessage()));
    }

    // @RequestParam type conversion failure (e.g. invalid enum value)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.info("request param type mismatch: param={}, value={}", e.getName(), e.getValue());
        String message = String.format("'%s' 파라미터에 유효하지 않은 값입니다: %s", e.getName(), e.getValue());
        return ResponseEntity.status(GlobalErrorCode.BAD_REQUEST_ERROR.getStatus())
                .body(Response.onFailure(GlobalErrorCode.BAD_REQUEST_ERROR.getCode(), message));
    }

    // service-layer @Validated constraint violation
    @ExceptionHandler(ConstraintViolationException.class)
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

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        log.error("unexpected error: {}", e.getMessage(), e);
        return ResponseEntity.status(GlobalErrorCode.SERVER_ERROR.getStatus())
                .body(Response.onFailure(
                        GlobalErrorCode.SERVER_ERROR.getCode(), GlobalErrorCode.SERVER_ERROR.getMessage()));
    }

    // @RequestBody/@ModelAttribute bean validation failure (Spring 6+ raises MethodArgumentNotValidException for both)
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.info("method argument validation failed: {}", e.getMessage());
        StringBuilder errorMessage = buildFieldErrors(e.getBindingResult().getAllErrors());
        return ResponseEntity.status(status)
                .body(Response.onFailure(GlobalErrorCode.BAD_REQUEST_ERROR.getCode(), errorMessage.toString()));
    }

    // controller-layer @Validated method parameter constraint violation (Spring 6+)
    @Override
    protected ResponseEntity<Object> handleHandlerMethodValidationException(
            HandlerMethodValidationException e, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.info("handler method validation failed: {}", e.getMessage());
        StringBuilder errorMessage = new StringBuilder();
        e.getAllValidationResults().forEach(result -> {
            String paramName = result.getMethodParameter().getParameterName();
            result.getResolvableErrors().forEach(error -> errorMessage
                    .append(paramName)
                    .append(": ")
                    .append(error.getDefaultMessage())
                    .append("\n"));
        });
        return ResponseEntity.status(status)
                .body(Response.onFailure(GlobalErrorCode.BAD_REQUEST_ERROR.getCode(), errorMessage.toString()));
    }

    // JSON body parse failure (malformed JSON, invalid enum in body, wrong type)
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException e, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.info("request body not readable: {}", e.getMessage());
        return ResponseEntity.status(status)
                .body(Response.onFailure(GlobalErrorCode.BAD_REQUEST_ERROR.getCode(), "JSON 형식에 맞지 않는 요청입니다."));
    }

    // required @RequestParam missing
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException e, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.info("required request param missing: param={}", e.getParameterName());
        String message = String.format("필수 파라미터 '%s'가 누락되었습니다.", e.getParameterName());
        return ResponseEntity.status(status)
                .body(Response.onFailure(GlobalErrorCode.BAD_REQUEST_ERROR.getCode(), message));
    }

    private StringBuilder buildFieldErrors(List<ObjectError> errors) {
        StringBuilder errorMessage = new StringBuilder();
        for (ObjectError error : errors) {
            String field = (error instanceof FieldError fe) ? fe.getField() : error.getObjectName();
            errorMessage
                    .append(field)
                    .append(": ")
                    .append(error.getDefaultMessage())
                    .append("\n");
        }
        return errorMessage;
    }
}
