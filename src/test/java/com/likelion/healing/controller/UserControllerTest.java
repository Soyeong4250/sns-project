package com.likelion.healing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.healing.domain.dto.UserJoinReq;
import com.likelion.healing.domain.dto.UserJoinRes;
import com.likelion.healing.domain.dto.UserLoginReq;
import com.likelion.healing.domain.dto.UserLoginRes;
import com.likelion.healing.exception.ErrorCode;
import com.likelion.healing.exception.HealingSnsAppException;
import com.likelion.healing.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    UserService userService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @WithMockUser
    @DisplayName("회원가입 성공")
    void successfulJoin() throws Exception {
        UserJoinReq req = UserJoinReq.builder()
                .userName("Soyeong")
                .password("12345")
                .build();

        given(userService.join(any(UserJoinReq.class))).willReturn(new UserJoinRes(1, "Soyeong"));

        mockMvc.perform(post("/api/v1/users/join")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.userId").value(1))
                .andExpect(jsonPath("$.result.userName").value("Soyeong"))
                .andDo(print());
    }

    @Test
    @WithMockUser
    @DisplayName("회원가입 실패 - userName 중복인 경우")
    void failedJoin() throws Exception {
        UserJoinReq req = UserJoinReq.builder()
                .userName("Soyeong")
                .password("12345")
                .build();

        given(userService.join(any(UserJoinReq.class))).willThrow(new HealingSnsAppException(ErrorCode.DUPLICATED_USER_NAME, String.format("%s은(는) 이미 있습니다.", req.getUserName())));

        mockMvc.perform(post("/api/v1/users/join")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(req)))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser
    @DisplayName("로그인 성공")
    void successfulLogin() throws Exception {
        UserLoginReq req = UserLoginReq.builder()
                .userName("Soyeong")
                .password("12345")
                .build();

        given(userService.login(any(UserLoginReq.class))).willReturn(new UserLoginRes("token"));

        mockMvc.perform(post("/api/v1/users/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.jwt").exists())
                .andDo(print());
    }

    @Test
    @WithMockUser
    @DisplayName("로그인 실패 - 존재하지 않는 회원")
    void notFoundUser() throws Exception {
        UserLoginReq req = UserLoginReq.builder()
                .userName("Soyeong")
                .password("12345")
                .build();

        given(userService.login(any(UserLoginReq.class))).willThrow(new HealingSnsAppException(ErrorCode.NOT_FOUND, String.format("%s은(는) 없는 회원입니다.", req.getUserName())));

        mockMvc.perform(post("/api/v1/users/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(req)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.result.errorCode").value("NOT_FOUND"))
                .andExpect(jsonPath("$.result.message").value(req.getUserName() + "은(는) 없는 회원입니다."))
                .andDo(print());
    }
}