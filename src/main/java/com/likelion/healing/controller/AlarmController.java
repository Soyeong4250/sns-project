package com.likelion.healing.controller;

import com.likelion.healing.domain.dto.AlarmRes;
import com.likelion.healing.service.AlarmService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@Api(tags = {"04. AlarmController"})
@RequestMapping("/api/v1/alarms")
public class AlarmController {

    private final AlarmService alarmService;

    @ApiOperation(value = "알람 조회", notes = "JWT와 페이징 조건(옵션)을 입력받아 알람 조회 성공유무를 반환")
    @ApiResponses({
            @ApiResponse(code = 200, message = "알람 조회 성공"),
            @ApiResponse(code = 404, message = "일치하는 회원 이름 없음 👉 USERNAME_NOT_FOUND, 에러 메세지 반환"),
    })
    @GetMapping
    public ResponseEntity<Response<Page<AlarmRes>>> getAlarms(Authentication authentication,
                                                              @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        log.debug("getAlarms() 실행");
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(Response.success(alarmService.getAlarms(authentication.getName(), pageable)));
    }

}
