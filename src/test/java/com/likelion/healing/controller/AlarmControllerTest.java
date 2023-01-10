package com.likelion.healing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.healing.domain.dto.AlarmRes;
import com.likelion.healing.domain.entity.AlarmType;
import com.likelion.healing.service.AlarmService;
import com.likelion.healing.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AlarmController.class)
class AlarmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    UserService userService;

    @MockBean
    AlarmService alarmService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @WithMockUser
    @DisplayName("알람 목록 조회 성공")
    void getAlarmsTest() throws Exception {
        List<AlarmRes> alarmList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            LocalDateTime now = LocalDateTime.now().plusMinutes((int)(Math.random()*10));
            alarmList.add(new AlarmRes(i, AlarmType.NEW_LIKE_ON_POST, 1, 1, now));
        }

        // JPA Repository의 PagingAndSortingRepository에서 Paging과 Sorting이 된 상태로 return
        alarmList = alarmList.stream().sorted(Comparator.comparing(AlarmRes::getCreatedAt).reversed()).collect(Collectors.toList());

        given(alarmService.getAlarms(any(String.class), any(Pageable.class))).willReturn(new PageImpl<>(alarmList));

        mockMvc.perform(get("/api/v1/alarms")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "createdAt,desc")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.content.length()").value(10))
                .andExpect(jsonPath("$.result.content[0].alarmType").value("NEW_LIKE_ON_POST"))
                .andExpect(jsonPath("$.result.content[0].fromUserId").value(1))
                .andExpect(jsonPath("$.result.content[0].targetId").value(1))
                .andExpect(jsonPath("$.result.content[0].text").value("new like!"))
                .andExpect(jsonPath("$.result.content[0].createdAt").exists());
    }

    @Test
    @WithAnonymousUser
    @DisplayName("알람 목록 조회 실패 - 로그인하지 않은 경우")
    void notLogin() throws Exception {
        List<AlarmRes> alarmList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            LocalDateTime now = LocalDateTime.now().plusMinutes((int)(Math.random()*10));
            alarmList.add(new AlarmRes(i, AlarmType.NEW_LIKE_ON_POST, 1, 1, now));
        }

        // JPA Repository의 PagingAndSortingRepository에서 Paging과 Sorting이 된 상태로 return
        alarmList = alarmList.stream().sorted(Comparator.comparing(AlarmRes::getCreatedAt).reversed()).collect(Collectors.toList());

        given(alarmService.getAlarms(any(String.class), any(Pageable.class))).willReturn(new PageImpl<>(alarmList));

        mockMvc.perform(get("/api/v1/users/alarms")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}