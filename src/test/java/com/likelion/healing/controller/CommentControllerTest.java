package com.likelion.healing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.healing.domain.dto.CommentReq;
import com.likelion.healing.domain.dto.CommentRes;
import com.likelion.healing.domain.dto.PostReq;
import com.likelion.healing.exception.ErrorCode;
import com.likelion.healing.exception.HealingSnsAppException;
import com.likelion.healing.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
@WithMockUser(roles = "USER")
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    CommentService commentService;

    @Autowired
    ObjectMapper objectMapper;

    private Integer postId;

    @BeforeEach
    void setUp() {
        postId = 3;
    }

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
            CommentRes comment = CommentRes.builder()
                                                .id(1)
                                                .comment("comment")
                                                .postId(postId)
                                                .userName("test")
                                                .createdAt(nowTime)
                                                .lastModifiedAt(nowTime)
                                                .build();

            given(commentService.createComment(any(Integer.class), any(CommentReq.class), any(String.class)))
                    .willReturn(comment);

            mockMvc.perform(post(String.format("/api/v1/posts/%d/comments", postId))
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
            CommentRes comment = CommentRes.builder()
                                                .id(1)
                                                .comment("comment")
                                                .postId(postId)
                                                .userName("test")
                                                .createdAt(nowTime)
                                                .lastModifiedAt(nowTime)
                                                .build();

            given(commentService.createComment(any(Integer.class), any(CommentReq.class), any(String.class)))
                    .willReturn(comment);

            mockMvc.perform(post(String.format("/api/v1/posts/%d/comments", postId))
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

    @Test
    @DisplayName("댓글 목록 조회 성공")
    void getCommentList() throws Exception {
        List<CommentRes> commentList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            LocalDateTime now = LocalDateTime.now();
            commentList.add(new CommentRes(i, "comment"+i, "test", postId, now, now));
        }


        given(commentService.getCommentList(any(Integer.class), any(Pageable.class))).willReturn(new PageImpl<>(commentList));

        mockMvc.perform(get(String.format("/api/v1/posts/%d/comments", postId))
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "createdAt,desc")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"));

    }

    @Nested
    @DisplayName("댓글 수정 테스트")
    class UpdateCommentTest {
        private Integer commentId = 1;

        @Test
        @DisplayName("댓글 수정 성공")
        void successfulUpdateComment() throws Exception {
            CommentReq req = CommentReq.builder()
                    .comment("update")
                    .build();

            LocalDateTime nowTime = LocalDateTime.now();
            CommentRes comment = CommentRes.builder()
                    .id(commentId)
                    .comment("update")
                    .postId(postId)
                    .userName("test")
                    .createdAt(nowTime)
                    .lastModifiedAt(nowTime)
                    .build();

            given(commentService.updateComment(any(Integer.class), any(Integer.class), any(CommentReq.class), any(String.class)))
                    .willReturn(comment);

            mockMvc.perform(put(String.format("/api/v1/posts/%d/comments/%d", postId, commentId))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(req)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                    .andExpect(jsonPath("$.result.id").value(1))
                    .andExpect(jsonPath("$.result.comment").value("update"))
                    .andExpect(jsonPath("$.result.postId").value(3))
                    .andExpect(jsonPath("$.result.userName").value("test"))
                    .andExpect(jsonPath("$.result.createdAt").exists())
                    .andExpect(jsonPath("$.result.lastModifiedAt").exists());
        }

        @Test
        @WithAnonymousUser
        @DisplayName("댓글 수정 실패 - 인증 실패")
        void NotFoundedUser() throws Exception {
            CommentReq req = CommentReq.builder()
                    .comment("update")
                    .build();

            String userName = "test";

            given(commentService.updateComment(any(Integer.class), any(Integer.class), any(CommentReq.class), any(String.class)))
                    .willThrow(new HealingSnsAppException(ErrorCode.USERNAME_NOT_FOUND, String.format("%s은(는) 없는 회원입니다", userName)));

            mockMvc.perform(put(String.format("/api/v1/posts/%d/comments/%d", postId, commentId))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(req)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("댓글 수정 실패 - 작성자 불일치")
        void update_mismatchedAuthorAndUser() throws Exception {
            CommentReq req = CommentReq.builder()
                    .comment("update")
                    .build();

            given(commentService.updateComment(any(Integer.class), any(Integer.class), any(CommentReq.class), any(String.class)))
                    .willThrow(new HealingSnsAppException(ErrorCode.INVALID_PERMISSION, "사용자가 권한이 없습니다"));

            mockMvc.perform(put(String.format("/api/v1/posts/%d/comments/%d", postId, commentId))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(req)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.resultCode").value("ERROR"))
                    .andExpect(jsonPath("$.result.errorCode").value("INVALID_PERMISSION"))
                    .andExpect(jsonPath("$.result.message").value("사용자가 권한이 없습니다"));
        }

        @Test
        @DisplayName("댓글 수정 실패 - 데이터베이스 에러")
        void update_notFoundDatabase() throws Exception {
            PostReq req = PostReq.builder()
                    .title("test title")
                    .body("test body")
                    .build();
            Integer postId = 1;

            given(commentService.updateComment(any(Integer.class), any(Integer.class), any(CommentReq.class), any(String.class)))
                    .willThrow(new HealingSnsAppException(ErrorCode.DATABASE_ERROR, "DB 에러"));

            mockMvc.perform(put(String.format("/api/v1/posts/%d/comments/%d", postId, commentId))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(req)))
                    .andDo(print())
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.resultCode").value("ERROR"))
                    .andExpect(jsonPath("$.result.errorCode").value("DATABASE_ERROR"))
                    .andExpect(jsonPath("$.result.message").value("DB 에러"));
        }
    }



}