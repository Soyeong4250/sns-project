package com.likelion.healing.controller;

import com.likelion.healing.domain.dto.UserJoinReq;
import com.likelion.healing.domain.dto.UserJoinRes;
import com.likelion.healing.domain.entity.Response;
import com.likelion.healing.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/join")
    public Response<UserJoinRes> join(@RequestBody UserJoinReq userJoinReq) {
        log.debug("join() 실행");
        UserJoinRes userDto = userService.join(userJoinReq);
        return Response.success(new UserJoinRes(userDto.getUserId(), userDto.getUserName()));
    }

}
