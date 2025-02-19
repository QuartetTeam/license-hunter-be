package quartet.server.api.example.controller;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import quartet.server.api.common.response.ApiResponse;
import quartet.server.api.example.dto.resquest.ExampleRequest;
import quartet.server.domain.example.exception.ExampleNotFoundException;

import static quartet.server.core.code.CommonSuccessCode.NO_CONTENT;
import static quartet.server.core.code.CommonSuccessCode.OK;

@RestController
@RequestMapping("/api/v1")
public class ExampleController { // todo: 이후 삭제 예정. 형식 참고 바람

    @PostMapping("/example/1")
    @Transactional
    public ApiResponse<ExampleRequest> test1(@Validated @RequestBody ExampleRequest exampleRequest){
        return ApiResponse.success(OK, exampleRequest); //성공 응답 방식 참고 - 응답 데이터 있는 경우
    }

    @PostMapping("/example/2")
    public ApiResponse<Void> test2(){
        return ApiResponse.success(NO_CONTENT); // 성공 응답 방식 참고 - 응답 데이터 없는 경우
    }

    @GetMapping("/example/3")
    public ApiResponse<ExampleRequest> test3(){
        throw new ExampleNotFoundException(); // 커스텀 예외처리 방식 참고
    }
}