package quartet.server.api.test.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import quartet.server.api.common.response.QuartetResponse;
import quartet.server.api.test.dto.resquest.TestRequest;
import quartet.server.core.code.ErrorCode;
import quartet.server.core.code.SuccessCode;
import quartet.server.core.exception.NotFoundException;

@RestController
@RequestMapping("/api/v1")
public class TestController { // todo: 이후 삭제 예정. 형식 참고 바람

    @PostMapping("/tests1")
    public QuartetResponse<TestRequest> test1(@Validated @RequestBody TestRequest testRequest){
        return QuartetResponse.success(SuccessCode.OK, testRequest); //성공 응답 방식 참고 - 데이터 있는 경우
    }

    @PostMapping("/tests2")
    public QuartetResponse<Void> test2(){
        return QuartetResponse.success(SuccessCode.NO_CONTENT); // 성공 응답 방식 참고 - 데이터 없는 경우
    }

    @GetMapping("/tests")
    public QuartetResponse<TestRequest> test3(){
        throw new NotFoundException(ErrorCode.MEMBER_NOT_FOUND); // 커스텀 예외처리 방식 참고
    }
}