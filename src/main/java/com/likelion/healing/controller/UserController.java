package com.likelion.healing.controller;

import com.likelion.healing.domain.dto.UserJoinReq;
import com.likelion.healing.domain.dto.UserJoinRes;
import com.likelion.healing.domain.dto.UserLoginReq;
import com.likelion.healing.domain.dto.UserLoginRes;
import com.likelion.healing.domain.entity.Response;
import com.likelion.healing.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@Api(tags = {"01. UserController"})
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @ApiOperation(value = "회원가입", notes = "중복된 회원이 없는 경우 200와 결과(userId, userName), 중복된 회원이 있는 경우 409와 결과(errorCode, message)를 반환")
    @PostMapping("/join")
    public Response<UserJoinRes> join(@RequestBody UserJoinReq userJoinReq) {
        log.debug("join() 실행");
        UserJoinRes userDto = userService.join(userJoinReq);
        return Response.success(new UserJoinRes(userDto.getUserId(), userDto.getUserName()));
    }

    @PostMapping("/login")
    public Response<UserLoginRes> login(@RequestBody UserLoginReq userJoinReq) {
        log.debug("login() 실행");
        return Response.success(new UserLoginRes("로그인 token"));
    }
}
