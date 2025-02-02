package quartet.server.core.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import quartet.server.core.code.ErrorCode;
import quartet.server.api.common.response.QuartetResponse;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    // 요청 본문 데이터 유효성 검사(@RequestBody)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected QuartetResponse<Void> handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.warn("MethodArgumentNotValidException", e);
        return QuartetResponse.fail(ErrorCode.INVALID_INPUT_VALUE, e.getBindingResult());
    }

    // 폼 데이터 및 URL 매개변수 유효성 검사(@ModelAttribute)
    @ExceptionHandler(BindException.class)
    protected QuartetResponse<Void> handleBindException(final BindException e) {
        log.warn(e.getMessage(), e);
        return QuartetResponse.fail(ErrorCode.INVALID_INPUT_VALUE, e.getBindingResult());
    }

    // 잘못된 형식의 데이터 전송
    @ExceptionHandler(HttpMessageConversionException.class)
    protected QuartetResponse<Void> handleHttpMessageConversionException(final HttpMessageConversionException e) {
        log.warn("[HttpMessageConversionException] ", e);
        return QuartetResponse.fail(ErrorCode.INVALID_INPUT_VALUE);
    }

    // 비즈니스 로직 예외
    @ExceptionHandler(CommonException.class)
    protected QuartetResponse<Void> handleCommonException(final CommonException e) {
        log.warn("[CommonException - " + e.getMessage() + "]", e);
        return QuartetResponse.fail(e.getErrorCode());
    }

    // 서버 에러
    @ExceptionHandler(Exception.class)
    protected QuartetResponse<Void> handleException(final Exception e) {
        log.error("[CommonException - " + e.getMessage() + "]", e);
        return QuartetResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR);
    }
}