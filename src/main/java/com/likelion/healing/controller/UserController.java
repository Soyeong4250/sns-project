package com.likelion.healing.controller;

import com.likelion.healing.domain.dto.UserJoinReq;
import com.likelion.healing.domain.entity.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/v1/users")
public class UserController {

    @PostMapping("/join")
    public Response<String> join(@RequestBody UserJoinReq userJoinReq) {
        log.debug("join() 실행");
        return Response.success("회원가입 성공");
    }

}
