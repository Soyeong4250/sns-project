package com.likelion.healing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.healing.domain.dto.PostReq;
import com.likelion.healing.domain.dto.PostRes;
import com.likelion.healing.domain.dto.PostViewRes;
import com.likelion.healing.domain.entity.User;
import com.likelion.healing.exception.ErrorCode;
import com.likelion.healing.exception.HealingSnsAppException;
import com.likelion.healing.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    PostService postService;
    PostController postController;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @WithMockUser
    @DisplayName("포스트 작성 성공")
    void successfulAddPost() throws Exception {
        PostReq req = PostReq.builder()
                .title("title1")
                .body("body1")
                .build();

        given(postService.addPost(any(PostReq.class), "Bearer " + any(String.class))).willReturn(new PostRes("포스트 등록 완료", 1));

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

    @Test
    @WithAnonymousUser
    @DisplayName("포스트 작성 실패 - JWT를 Bearer Token으로 보내지 않은 경우")
    void notStartsWithBearer() throws Exception {
        PostReq req = PostReq.builder()
                .title("title1")
                .body("body1")
                .build();

        given(postService.addPost(any(PostReq.class), any(String.class))).willThrow(new HealingSnsAppException(ErrorCode.INVALID_PERMISSION, "사용자가 권한이 없습니다."));

        mockMvc.perform(post("/api/v1/posts")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(req)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    @DisplayName("포스트 작성 실패 - JWT가 유효하지 않은 경우")
    void expiredToken() throws Exception {
        PostReq req = PostReq.builder()
                .title("title1")
                .body("body1")
                .build();

        given(postService.addPost(any(PostReq.class), "Bearer " + any(String.class))).willThrow(new HealingSnsAppException(ErrorCode.INVALID_PERMISSION, "사용자가 권한이 없습니다."));

        mockMvc.perform(post("/api/v1/posts")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(req)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    @DisplayName("포스트 전체 목록 조회 - 생성일자 내림차순")
    void getPostList() throws Exception {

        List<PostViewRes> postList = new ArrayList<>();
        List<LocalDateTime> timeList = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            LocalDateTime now = LocalDateTime.now();
            timeList.add(now);
            postList.add(new PostViewRes(i, "title"+(i+1), "body"+(i+1), "Soyeong", now, now));
        }
        timeList.stream().sorted(LocalDateTime::compareTo);
        for (LocalDateTime time:timeList) {
            System.out.println(time);
        }
        Page<PostViewRes> pageRes = new PageImpl<>(postList);
        given(postService.getPostList(any(Pageable.class))).willReturn(pageRes);

        mockMvc.perform(get("/api/v1/posts"))
                .andExpect(status().isOk())
                .andDo(print());

    }

    @Test
    @WithMockUser
    @DisplayName("포스트 단건 조회 성공")
    void successfulGetPostById() throws Exception {
        Integer postId = 1;
        PostViewRes post = PostViewRes.builder()
                .id(1)
                .title("title1")
                .body("body1")
                .userName("Soyeong")
                .createdAt(LocalDateTime.now())
                .lastModifiedAt(LocalDateTime.now())
                .build();

        given(postService.getPostById(any(Integer.class))).willReturn(post);

        mockMvc.perform(get(String.format("/api/v1/posts/%d", postId))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.id").value(1))
                .andExpect(jsonPath("$.result.title").value("title1"))
                .andExpect(jsonPath("$.result.body").value("body1"))
                .andExpect(jsonPath("$.result.userName").value("Soyeong"))
                .andExpect(jsonPath("$.result.createdAt").exists())
                .andExpect(jsonPath("$.result.lastModifiedAt").exists());
    }

    @Test
    @WithAnonymousUser
    @DisplayName("포스트 수정 실패 - 인증 실패")
    void update_authenticationFailed() throws Exception {
        PostReq req = PostReq.builder()
                .title("test title")
                .body("test body")
                .build();
        User user = User.builder()
                .userName("Soyeong")
                .password("12345")
                .build();
        Integer postId = 1;

        given(postService.updatePostById(any(Integer.class), any(PostReq.class), any(String.class))).willThrow(new HealingSnsAppException(ErrorCode.USERNAME_NOT_FOUND, String.format("%s은(는) 없는 회원입니다.", user.getUsername())));

        mockMvc.perform(put(String.format("/api/v1/posts/%d", postId))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(req)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    @DisplayName("포스트 수정 실패 - 작성자 불일치")
    void update_mismatchedAuthorAndUser() throws Exception {
        PostReq req = PostReq.builder()
                .title("test title")
                .body("test body")
                .build();
        Integer postId = 1;

        given(postService.updatePostById(any(Integer.class), any(PostReq.class), any(String.class))).willThrow(new HealingSnsAppException(ErrorCode.INVALID_PERMISSION, "사용자가 권한이 없습니다."));

        mockMvc.perform(put(String.format("/api/v1/posts/%d", postId))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(req)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.errorCode").value("INVALID_PERMISSION"))
                .andExpect(jsonPath("$.result.message").value("사용자가 권한이 없습니다."));
    }

    @Test
    @WithMockUser
    @DisplayName("포스트 수정 실패 - 데이터베이스 에러")
    void update_notFoundDatabase() throws Exception {
        PostReq req = PostReq.builder()
                .title("test title")
                .body("test body")
                .build();
        Integer postId = 1;

        given(postService.updatePostById(any(Integer.class), any(PostReq.class), any(String.class))).willThrow(new HealingSnsAppException(ErrorCode.DATABASE_ERROR, "DB에러"));

        mockMvc.perform(put(String.format("/api/v1/posts/%d", postId))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(req)))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.errorCode").value("DATABASE_ERROR"))
                .andExpect(jsonPath("$.result.message").value("DB에러"));
    }

    @Test
    @WithMockUser
    @DisplayName("포스트 수정 성공")
    void successfulEdit() throws Exception {
        PostReq req = PostReq.builder()
                .title("test title")
                .body("test body")
                .build();
        Integer postId = 1;

        given(postService.updatePostById(any(Integer.class), any(PostReq.class), any(String.class))).willReturn(new PostRes("포스트 수정 완료", postId));

        mockMvc.perform(put(String.format("/api/v1/posts/%d", postId))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(req)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.message").value("포스트 수정 완료"))
                .andExpect(jsonPath("$.result.postId").value(postId));
    }

    @Test
    @WithMockUser
    @DisplayName("포스트 삭제 성공")
    void successfulDelete() throws Exception {
        Integer postId = 1;

        given(postService.deletePostById(any(Integer.class), any(String.class))).willReturn(new PostRes("포스트 삭제 완료", postId));

        mockMvc.perform(delete(String.format("/api/v1/posts/%d", postId))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.message").value("포스트 삭제 완료"))
                .andExpect(jsonPath("$.result.postId").value(postId));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("포스트 삭제 실패 - 인증 실패")
    void delete_authenticationFailed() throws Exception {
        User user = User.builder()
                .userName("Soyeong")
                .password("12345")
                .build();
        Integer postId = 1;

        given(postService.deletePostById(any(Integer.class), any(String.class))).willThrow(new HealingSnsAppException(ErrorCode.USERNAME_NOT_FOUND, String.format("%s은(는) 없는 회원입니다.", user.getUsername())));

        mockMvc.perform(delete(String.format("/api/v1/posts/%d", postId))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    @DisplayName("포스트 삭제 실패 - 작성자 불일치")
    void delete_mismatchedAuthorAndUser() throws Exception {
        Integer postId = 1;

        given(postService.deletePostById(any(Integer.class), any(String.class))).willThrow(new HealingSnsAppException(ErrorCode.INVALID_PERMISSION, "사용자가 권한이 없습니다."));

        mockMvc.perform(delete(String.format("/api/v1/posts/%d", postId))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.errorCode").value("INVALID_PERMISSION"))
                .andExpect(jsonPath("$.result.message").value("사용자가 권한이 없습니다."));
    }

    @Test
    @WithMockUser
    @DisplayName("포스트 삭제 실패 - 데이터베이스 에러")
    void delete_notFoundDatabase() throws Exception {
        Integer postId = 1;

        given(postService.deletePostById(any(Integer.class), any(String.class))).willThrow(new HealingSnsAppException(ErrorCode.DATABASE_ERROR, "DB에러"));

        mockMvc.perform(delete(String.format("/api/v1/posts/%d", postId))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.errorCode").value("DATABASE_ERROR"))
                .andExpect(jsonPath("$.result.message").value("DB에러"));
    }
}