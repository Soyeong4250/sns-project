package com.likelion.healing.controller;

import com.likelion.healing.domain.dto.*;
import com.likelion.healing.domain.entity.UserRole;
import com.likelion.healing.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Slf4j
@RequiredArgsConstructor
@Api(tags = {"01. UserController"})
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @ApiOperation(value = "회원가입", notes = "userName과 password를 입력받아 회원가입 성공유무를 반환")
    @ApiResponses({
            @ApiResponse(code = 200, message = "회원가입 성공"),
            @ApiResponse(code = 409, message = "중복 회원 존재 👉 DUPLICATED_USER_NAME, 에러 메세지 반환"),
    })
    @PostMapping("/join")
    public Response<UserJoinRes> join(@Valid @RequestBody UserJoinReq userJoinReq) {
        log.debug("join() 실행");
        UserJoinRes userDto = userService.join(userJoinReq);
        return Response.success(new UserJoinRes(userDto.getUserId(), userDto.getUserName()));
    }

    @ApiOperation(value = "로그인", notes = "userName과 password를 입력받아 로그인 성공유무를 반환")
    @ApiResponses({
            @ApiResponse(code = 200, message = "로그인 성공"),
            @ApiResponse(code = 404, message = "일치하는 회원 이름 없음 👉 USERNAME_NOT_FOUND, 에러 메세지 반환"),
            @ApiResponse(code = 401, message = "비밀번호 일치하지 않음 👉 INVALID_PASSWORD, 에러 메세지 반환")
    })
    @PostMapping("/login")
    public Response<UserLoginRes> login(@Valid @RequestBody UserLoginReq userLoginReq) {
        log.debug("login() 실행");
        return Response.success(userService.login(userLoginReq));
    }

    @ApiOperation(value = "회원 권한 변경", notes = "변경할 권한을 입력받아 권한 변경 성공유무를 반환")
    @ApiResponses({
            @ApiResponse(code = 200, message = "권한 변경 성공"),
            @ApiResponse(code = 403, message = "접근 권한 없음"),
            @ApiResponse(code = 404, message = "일치하는 회원 이름 없음 👉 USERNAME_NOT_FOUND, 에러 메세지 반환"),
    })
    @Secured(UserRole.Authority.ADMIN)
    @PostMapping("/{userId}/role/change")
    public Response<UserRoleUpdateRes> updateRole(@PathVariable Integer userId, @RequestBody UserRoleUpdateReq role, Authentication authentication) {
        log.info("authentication.getAuthorities : {}", authentication.getAuthorities());
        UserRoleUpdateRes updateUserRoleRes = userService.changeRole(userId, role.getRole(), authentication.getName());
        return Response.success(updateUserRoleRes);
    }
}
