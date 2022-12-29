package com.likelion.healing.controller;

import com.likelion.healing.service.AlgorithmService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@Api(tags = {"00. HelloController"})
@RequestMapping("/api/v1/hello")
public class HelloController {

    private final AlgorithmService algorithmService;

    @ApiOperation(value = "cicd test 출력", notes = "GetMapping를 이용하여 cicd test 문자열을 출력합니다.")
    @GetMapping()
    public String printHello() {
        log.debug("printHello() 실행");
        return "이소영";
    }

    @ApiOperation(value = "모든 자릿수의 합 출력해보기", notes = "PostMapping을 이용하여 sumOfDigit 결과를 출력합니다.")
    @GetMapping("/{num}")
    public Integer sumOfDigit(@PathVariable Integer num) {
        return algorithmService.sumOfDigit(num);
    }

}
