package quartet.server.core.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import quartet.server.api.common.response.ApiResponse;
import quartet.server.core.code.CommonErrorCode;
import quartet.server.domain.calender.exception.CalendarException;
import quartet.server.domain.example.exception.ExampleException;
import quartet.server.domain.mail.exception.MailException;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ApiResponse<Void> handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.warn("[MethodArgumentNotValidException]", e);
        return ApiResponse.fail(CommonErrorCode.INVALID_INPUT_VALUE, e.getBindingResult());
    }

    @ExceptionHandler(BindException.class)
    protected ApiResponse<Void> handleBindException(final BindException e) {
        log.warn("[BindException] : {}", e.getMessage(), e);
        return ApiResponse.fail(CommonErrorCode.INVALID_INPUT_VALUE, e.getBindingResult());
    }

    @ExceptionHandler(HttpMessageConversionException.class)
    protected ApiResponse<Void> handleHttpMessageConversionException(final HttpMessageConversionException e) {
        log.warn("[HttpMessageConversionException]", e);
        return ApiResponse.fail(CommonErrorCode.INVALID_INPUT_VALUE);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ApiResponse<Void> handleHttpRequestMethodNotSupportedException(final HttpRequestMethodNotSupportedException e) {
        log.warn("[HttpRequestMethodNotSupportedException]", e);
        return ApiResponse.fail(CommonErrorCode.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    protected ApiResponse<Void> handleNoHandlerFoundException(final NoHandlerFoundException e) {
        log.warn("[NoHandlerFoundException]", e);
        return ApiResponse.fail(CommonErrorCode.URL_NOT_FOUND);
    }

    @ExceptionHandler(ExampleException.class)
    protected ApiResponse<Void> handleExampleException(final ExampleException e) {
        log.warn("[ExampleException] : {}", e.getMessage(), e);
        return ApiResponse.fail(e.getErrorCode());
    }
    @ExceptionHandler(CalendarException.class)
    protected ApiResponse<Void> handleCalendarException(final CalendarException e) {
        log.warn("[CalendarException] : {}", e.getMessage(), e);
        return ApiResponse.fail(e.getErrorCode());
    }

    @ExceptionHandler(MailException.class)
    protected ApiResponse<Void> handleMailException(final MailException e) {
        log.warn("[MailException] : {}", e.getMessage(), e);
        return ApiResponse.fail(e.getErrorCode());
    }

    @ExceptionHandler(BaseException.class)
    protected ApiResponse<Void> handleBaseException(final BaseException e) {
        log.warn("[BaseException] : {}", e.getMessage(), e);
        return ApiResponse.fail(e.getErrorCode());
    }

    @ExceptionHandler(Exception.class)
    protected ApiResponse<Void> handleException(final Exception e) {
        log.error("[Exception] : {}", e.getMessage(), e);
        return ApiResponse.fail(CommonErrorCode.INTERNAL_SERVER_ERROR);
    }
}