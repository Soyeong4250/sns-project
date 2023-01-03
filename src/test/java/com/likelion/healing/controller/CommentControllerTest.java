package com.likelion.healing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.healing.domain.dto.CommentAddRes;
import com.likelion.healing.domain.dto.CommentReq;
import com.likelion.healing.exception.ErrorCode;
import com.likelion.healing.exception.HealingSnsAppException;
import com.likelion.healing.service.CommentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
@WithMockUser(username = "test", roles = "USER")
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    CommentService commentService;

    @Autowired
    ObjectMapper objectMapper;

    @Nested
    @DisplayName("댓글 등록 테스트")
    class AddCommentTest{

        @Test
        @DisplayName("댓글 등록 성공")
        void successfulAddComment() throws Exception {
            CommentReq req = CommentReq.builder()
                    .comment("comment")
                    .build();

            LocalDateTime nowTime = LocalDateTime.now();
            CommentAddRes comment = CommentAddRes.builder()
                                                .id(1)
                                                .comment("comment")
                                                .postId(3)
                                                .userName("test")
                                                .createdAt(nowTime)
                                                .lastModifiedAt(nowTime)
                                                .build();

            given(commentService.createComment(any(Integer.class), any(CommentReq.class), any(String.class)))
                    .willReturn(comment);

            mockMvc.perform(post(String.format("/api/v1/posts/%d/comments", 3))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(req)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                    .andExpect(jsonPath("$.result.id").value(1))
                    .andExpect(jsonPath("$.result.comment").value("comment"))
                    .andExpect(jsonPath("$.result.postId").value(3))
                    .andExpect(jsonPath("$.result.userName").value("test"))
                    .andExpect(jsonPath("$.result.createdAt").exists())
                    .andExpect(jsonPath("$.result.lastModifiedAt").exists());
        }

        @Test
        @WithAnonymousUser
        @DisplayName("댓글 등록 실패 - 로그인 하지 않은 경우")
        void NotLogin() throws Exception {
            CommentReq req = CommentReq.builder()
                    .comment("comment")
                    .build();

            LocalDateTime nowTime = LocalDateTime.now();
            CommentAddRes comment = CommentAddRes.builder()
                                                .id(1)
                                                .comment("comment")
                                                .postId(3)
                                                .userName("test")
                                                .createdAt(nowTime)
                                                .lastModifiedAt(nowTime)
                                                .build();

            given(commentService.createComment(any(Integer.class), any(CommentReq.class), any(String.class)))
                    .willReturn(comment);

            mockMvc.perform(post(String.format("/api/v1/posts/%d/comments", 3))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(req)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());

            verify(commentService, never()).createComment(any(Integer.class), any(CommentReq.class), any(String.class));
        }

        @Test
        @DisplayName("댓글 등록 실패 - 게시글이 존재하지 않는 경우")
        void NotFoundedPost() throws Exception {
            CommentReq req = CommentReq.builder()
                    .comment("comment")
                    .build();

            Integer postId = 3;

            given(commentService.createComment(any(Integer.class), any(CommentReq.class), any(String.class)))
                    .willThrow(new HealingSnsAppException(ErrorCode.POST_NOT_FOUND, String.format("%d번 게시글은 존재하지 않습니다.", postId)));

            mockMvc.perform(post(String.format("/api/v1/posts/%d/comments", postId))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(req)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.resultCode").value("ERROR"))
                    .andExpect(jsonPath("$.result.errorCode").value("POST_NOT_FOUND"))
                    .andExpect(jsonPath("$.result.message").value(postId + "번 게시글은 존재하지 않습니다."));
        }
    }


}