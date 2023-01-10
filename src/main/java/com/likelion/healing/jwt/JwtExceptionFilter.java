package com.likelion.healing.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.healing.exception.ErrorCode;
import com.likelion.healing.exception.ErrorResult;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.NestedServletException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.NoSuchElementException;

@Slf4j
public class JwtExceptionFilter extends OncePerRequestFilter {


    // JWT 관련 오류만 잡아내기
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
            // null 이기에 값을 읽을 수 없음
        } catch (NestedServletException e) {
            sendResponseError(response, ErrorCode.INVALID_TOKEN.getMessage());
        } catch (ExpiredJwtException e) {
            // 토큰의 유효기간이 만료되었을 경우
            sendResponseError(response, ErrorCode.INVALID_TOKEN.getMessage());
        } catch (JwtException | IllegalArgumentException e) {
            // 토큰이 유효하지 않을 경우
            sendResponseError(response, ErrorCode.INVALID_TOKEN.getMessage());
        } catch (NoSuchElementException e){
            // 토큰 정보에 있는 유저가 DB에 없을 경우
            sendResponseError(response, ErrorCode.DATABASE_ERROR.getMessage());
        }

    }

    //한글 출력을 위해 getWriter() 사용
    private void sendResponseError(HttpServletResponse response, String message) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(objectMapper.writeValueAsString(new ErrorResult(ErrorCode.INVALID_TOKEN, message)));
    }
}
