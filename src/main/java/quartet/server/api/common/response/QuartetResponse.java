package quartet.server.api.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import quartet.server.core.code.ErrorCode;
import quartet.server.core.code.SuccessCode;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class QuartetResponse<T> {

    private final int code;
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T data;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final List<FieldErrorResponse> errors;

    // 성공 응답 (데이터 X)
    public static <T> QuartetResponse<T> success(SuccessCode successCode) {
        return new QuartetResponse<>(successCode.getStatusValue(), successCode.getMessage(), null, List.of());
    }

    // 성공 응답 (데이터 O)
    public static <T> QuartetResponse<T> success(SuccessCode successCode, T data) {
        return new QuartetResponse<>(successCode.getStatusValue(), successCode.getMessage(), data, List.of());
    }

    // 실패 응답 (에러 코드만)
    public static <T> QuartetResponse<T> fail(ErrorCode errorCode) {
        return new QuartetResponse<>(errorCode.getStatusValue(), errorCode.getMessage(), null, List.of());
    }

    // 실패 응답 (유효성 검사 오류 포함)
    public static <T> QuartetResponse<T> fail(ErrorCode errorCode, BindingResult bindingResult) {
        return new QuartetResponse<>(errorCode.getStatusValue(), errorCode.getMessage(), null, FieldErrorResponse.of(bindingResult));
    }

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class FieldErrorResponse {
        private final String field;
        private final String reason;

        public static List<FieldErrorResponse> of(final BindingResult bindingResult) {
            return bindingResult.getFieldErrors().stream()
                    .map(error -> new FieldErrorResponse(error.getField(), error.getDefaultMessage()))
                    .collect(Collectors.toList());
        }
    }
}
