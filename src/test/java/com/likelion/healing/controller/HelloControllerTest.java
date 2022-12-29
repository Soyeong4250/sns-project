package com.likelion.healing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.healing.exception.ErrorCode;
import com.likelion.healing.exception.HealingSnsAppException;
import com.likelion.healing.service.AlgorithmService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HelloController.class)
class HelloControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    AlgorithmService algorithmService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @WithMockUser
    @DisplayName("sumOfDigit 성공")
    void successfulSumOfDigit() throws Exception {
        Integer num = 6567;
        Integer sum = 24;

        given(algorithmService.sumOfDigit(any(Integer.class))).willReturn(sum);

        mockMvc.perform(post(String.format("/api/v1/hello/%d", num))
                .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @WithMockUser
    @DisplayName("sumOfDigit 실패 - 0이하의 수인 경우")
    void failedSumOfDigit() throws Exception {
        Integer num = 0;
        Integer sum = 24;

        given(algorithmService.sumOfDigit(any(Integer.class))).willThrow(new HealingSnsAppException(ErrorCode.BY_ZERO_ERROR, "0보다 큰 수를 입력해주세요."));

        mockMvc.perform(post(String.format("/api/v1/hello/%d", num))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.errorCode").value("BY_ZERO_ERROR"))
                .andExpect(jsonPath("$.result.message").value("0보다 큰 수를 입력해주세요."))
                .andDo(print());
    }
}