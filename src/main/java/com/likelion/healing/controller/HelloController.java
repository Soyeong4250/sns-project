package com.likelion.healing.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@Api(tags = {"00. HelloController"})
@RequestMapping("/api/v1/hello")
public class HelloController {

    @ApiOperation(value = "hello 출력", notes = "Get Method를 이용하여 hello 문자열을 출력합니다.")
    @GetMapping()
    public String printHello() {
        log.debug("printHello() 실행");
        return "hello";
    }
}
