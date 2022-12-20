package com.likelion.healing.controller;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/v1/hello")
public class HelloController {

    @ApiOperation(value = "hello 출력", notes = "Get Method")
    @GetMapping()
    public String printHello() {
        log.debug("printHello() 실행");
        return "hello";
    }
}
