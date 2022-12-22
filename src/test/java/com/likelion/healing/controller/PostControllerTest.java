package com.likelion.healing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.healing.domain.dto.PostAddReq;
import com.likelion.healing.domain.dto.PostAddRes;
import com.likelion.healing.service.PostService;
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

@WebMvcTest(PostController.class)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    PostService postService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @WithMockUser
    @DisplayName("포스트 작성 성공")
    void successfulAddPost() throws Exception {
        PostAddReq req = PostAddReq.builder()
                .title("title1")
                .body("body1")
                .build();

        given(postService.addPost(any(PostAddReq.class), any(String.class))).willReturn(new PostAddRes("포스트 등록 완료", 1));

        mockMvc.perform(post("/api/v1/posts")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(req)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.message").value("포스트 등록 완료"))
                .andExpect(jsonPath("$.result.postId").value(1));
    }
}