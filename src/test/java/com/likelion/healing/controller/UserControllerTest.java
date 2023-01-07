package com.likelion.healing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.healing.domain.dto.*;
import com.likelion.healing.domain.entity.AlarmType;
import com.likelion.healing.exception.ErrorCode;
import com.likelion.healing.exception.HealingSnsAppException;
import com.likelion.healing.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @Nested
    @DisplayName("회원가입 테스트")
    class JoinTest {

        @Test
        @WithMockUser
        @DisplayName("회원가입 성공")
        void successfulJoin() throws Exception {
            UserJoinReq req = UserJoinReq.builder()
                    .userName("Soyeong")
                    .password("12345678")
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
                    .password("12345678")
                    .build();

            given(userService.join(any(UserJoinReq.class))).willThrow(new HealingSnsAppException(ErrorCode.DUPLICATED_USER_NAME, String.format("%s은(는) 이미 있습니다.", req.getUserName())));

            mockMvc.perform(post("/api/v1/users/join")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(req)))
                    .andDo(print())
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("로그인 테스트")
    class LoginTest {

        @Test
        @WithMockUser
        @DisplayName("로그인 성공")
        void successfulLogin() throws Exception {
            UserLoginReq req = UserLoginReq.builder()
                    .userName("Soyeong")
                    .password("12345678")
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
                    .password("12345678")
                    .build();

            given(userService.login(any(UserLoginReq.class))).willThrow(new HealingSnsAppException(ErrorCode.USERNAME_NOT_FOUND, String.format("%s은(는) 없는 회원입니다.", req.getUserName())));

            mockMvc.perform(post("/api/v1/users/login")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(req)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.result.errorCode").value("USERNAME_NOT_FOUND"))
                    .andExpect(jsonPath("$.result.message").value(req.getUserName() + "은(는) 없는 회원입니다."))
                    .andDo(print());
        }

        @Test
        @WithMockUser
        @DisplayName("로그인 실패 - 비밀번호가 일치하지 않는 경우")
        void invalidPassword() throws Exception {
            UserLoginReq req = UserLoginReq.builder()
                    .userName("Soyeong")
                    .password("12345678")
                    .build();

            given(userService.login(any(UserLoginReq.class))).willThrow(new HealingSnsAppException(ErrorCode.INVALID_PASSWORD, "회원 이름 또는 비밀번호를 다시 확인해주세요."));

            mockMvc.perform(post("/api/v1/users/login")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(req)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.result.errorCode").value("INVALID_PASSWORD"))
                    .andExpect(jsonPath("$.result.message").value("회원 이름 또는 비밀번호를 다시 확인해주세요."))
                    .andDo(print());
        }
    }

    /*@Test
    @WithMockUser
    @DisplayName("권한 수정 성공 - ADMIN")
    void changeRoleTest() throws Exception {
        UserRoleUpdateReq req = UserRoleUpdateReq.builder()
                                                .role(UserRole.ADMIN)
                                                .build();
        UserRoleUpdateRes res = UserRoleUpdateRes.builder()
                                                .message("권한 변경 완료")
                                                .role(UserRole.ADMIN)
                                                .build();

        given(userService.changeRole(any(Integer.class), any(UserRole.class), any(String.class), any(String.class)))
                .willReturn(res);

        mockMvc.perform(post(String.format("/api/v1/users/%d/role/change", 3))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(req)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.message").value("권한 변경 완료"))
                .andExpect(jsonPath("$.result.role").value(UserRole.ADMIN));
    }*/

    @Test
    @WithMockUser
    @DisplayName("알람 목록 조회 성공")
    void getAlarmsTest() throws Exception {
        AlarmRes res = AlarmRes.builder()
                                .id(1)
                                .alarmType(AlarmType.NEW_LIKE_ON_POST)
                                .fromUserId(1)
                                .targetId(1)
                                .createdAt(LocalDateTime.now())
                                .build();

        given(userService.getAlarms(any(String.class), any(Pageable.class))).willReturn(res);

        mockMvc.perform(get("/api/v1/users/alarms")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.alarmType").value("NEW_LIKE_ON_POST"))
                .andExpect(jsonPath("$.result.fromUserId").value(1))
                .andExpect(jsonPath("$.result.targetId").value(1))
                .andExpect(jsonPath("$.result.text").value("new like!"))
                .andExpect(jsonPath("$.result.createdAt").exists());
    }

    @Test
    @WithAnonymousUser
    @DisplayName("알람 목록 조회 실패 - 로그인하지 않은 경우")
    void notLogin() throws Exception {
        AlarmRes res = AlarmRes.builder()
                .id(1)
                .alarmType(AlarmType.NEW_LIKE_ON_POST)
                .fromUserId(1)
                .targetId(1)
                .createdAt(LocalDateTime.now())
                .build();

        given(userService.getAlarms(any(String.class), any(Pageable.class))).willReturn(res);

        mockMvc.perform(get("/api/v1/users/alarms")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}