package com.likelion.healing.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.healing.exception.ErrorCode;
import com.likelion.healing.exception.ErrorResult;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {

        //한글 출력을 위해 getWriter() 사용
        ObjectMapper objectMapper = new ObjectMapper();
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(objectMapper.writeValueAsString(new ErrorResult(ErrorCode.FORBIDDEN_USERROLE, "접근 권한이 없습니다.")));

    }
}
