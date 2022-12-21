package com.likelion.healing.util;

import com.likelion.healing.domain.entity.User;
import com.likelion.healing.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final String secretKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        log.info("authorizationHeader : {}", authorizationHeader);

        if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            log.error("인증헤더가 잘못 되었습니다.");
            filterChain.doFilter(request, response);
            return;
        }

        String token;
        try{
            token = authorizationHeader.split(" ")[1];
        }catch (Exception e) {
            log.error("token 추출에 실패했습니다. ", e);
            filterChain.doFilter(request, response);
            return;
        }

        if(JwtTokenUtil.isExpired(token, secretKey)) {
            filterChain.doFilter(request, response);
            return;
        }

        String userName = JwtTokenUtil.getUserName(token, secretKey);
        log.info("userName : {}", userName);

        User user = userService.getUserByUserName(userName);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword(), List.of(new SimpleGrantedAuthority(user.getRole().name())));
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }
}
