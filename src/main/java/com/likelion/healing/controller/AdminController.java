package com.likelion.healing.controller;

import com.likelion.healing.domain.dto.AdminViewRes;
import com.likelion.healing.domain.entity.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/v1/admin")
public class AdminController {

    @GetMapping("/users")
    public Response<AdminViewRes> getAllUsers() {
        log.debug("getAllUsers() 실행");
        
        return Response.success(null);
    }
}
