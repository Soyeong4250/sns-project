package com.likelion.healing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.healing.domain.dto.PostReq;
import com.likelion.healing.domain.dto.PostRes;
import com.likelion.healing.domain.dto.PostViewRes;
import com.likelion.healing.domain.entity.UserEntity;
import com.likelion.healing.exception.ErrorCode;
import com.likelion.healing.exception.HealingSnsAppException;
import com.likelion.healing.fixture.TestInfoFixture;
import com.likelion.healing.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
import java.util.Comparator;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
@WithMockUser(username = "Soyeong")
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    PostService postService;

    @Autowired
    ObjectMapper objectMapper;

    @Nested
    @DisplayName("포스트 작성 테스트")
    class CreatePostTest{

        @Test
        @DisplayName("포스트 작성 성공")
        void successCreatePost() throws Exception {
            PostReq req = PostReq.builder()
                    .title("title1")
                    .body("body1")
                    .build();

            given(postService.createPost(any(PostReq.class), "Bearer " + any(String.class))).willReturn(new PostRes("포스트 등록 완료", 1));

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

            given(postService.createPost(any(PostReq.class), any(String.class))).willThrow(new HealingSnsAppException(ErrorCode.INVALID_PERMISSION, "사용자가 권한이 없습니다."));

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

            given(postService.createPost(any(PostReq.class), "Bearer " + any(String.class))).willThrow(new HealingSnsAppException(ErrorCode.INVALID_PERMISSION, "사용자가 권한이 없습니다."));

            mockMvc.perform(post("/api/v1/posts")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(req)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        }
    }

    @Test
    @DisplayName("포스트 전체 목록 조회 - 생성일자 내림차순")
    void getPostList() throws Exception {
        List<PostViewRes> postList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            LocalDateTime now = LocalDateTime.now().plusMinutes(i);
            postList.add(new PostViewRes(i, "title"+i, "body"+i, "test", now, now));
        }
        Page<PostViewRes> postViewResPage = new PageImpl<>(postList);
        postViewResPage.stream().sorted(Comparator.comparing(PostViewRes::getCreatedAt).reversed());

        given(postService.getPostList(any(Pageable.class))).willReturn(postViewResPage);

        mockMvc.perform(get("/api/v1/posts")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "createdAt,desc"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"));

    }

    @Test
    @DisplayName("포스트 단건 조회 성공")
    void successGetPostById() throws Exception {
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
                        .with(csrf())
                         .contentType(MediaType.APPLICATION_JSON))
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

    @Nested
    @DisplayName("포스트 수정 테스트")
    class UpdatePostTest {

        @Test
        @WithAnonymousUser
        @DisplayName("포스트 수정 실패 - 인증 실패")
        void authenticationFailed() throws Exception {
            PostReq req = PostReq.builder()
                    .title("test title")
                    .body("test body")
                    .build();
            UserEntity user = UserEntity.builder()
                    .userName("Soyeong")
                    .password("12345")
                    .build();
            Integer postId = 1;

            given(postService.updatePostById(any(Integer.class), any(PostReq.class), any(String.class), any(String.class))).willThrow(new HealingSnsAppException(ErrorCode.USERNAME_NOT_FOUND, String.format("%s은(는) 없는 회원입니다.", user.getUsername())));

            mockMvc.perform(put(String.format("/api/v1/posts/%d", postId))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(req)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("포스트 수정 실패 - 작성자 불일치")
        void mismatchedAuthorAndUser() throws Exception {
            PostReq req = PostReq.builder()
                    .title("test title")
                    .body("test body")
                    .build();
            Integer postId = 1;

            given(postService.updatePostById(any(Integer.class), any(PostReq.class), any(String.class), any(String.class))).willThrow(new HealingSnsAppException(ErrorCode.INVALID_PERMISSION, "사용자가 권한이 없습니다."));

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
        @DisplayName("포스트 수정 실패 - 데이터베이스 에러")
        void notFoundDatabase() throws Exception {
            PostReq req = PostReq.builder()
                    .title("test title")
                    .body("test body")
                    .build();
            Integer postId = 1;

            given(postService.updatePostById(any(Integer.class), any(PostReq.class), any(String.class), any(String.class))).willThrow(new HealingSnsAppException(ErrorCode.DATABASE_ERROR, "DB에러"));

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
        @DisplayName("포스트 수정 성공")
        void successUpdatePost() throws Exception {
            PostReq req = PostReq.builder()
                    .title("test title")
                    .body("test body")
                    .build();
            Integer postId = 1;

            given(postService.updatePostById(any(Integer.class), any(PostReq.class), any(String.class), any(String.class))).willReturn(new PostRes("포스트 수정 완료", postId));

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
    }


    @Nested
    @DisplayName("포스트 삭제 테스트")
    class DeletePostTest {

        @Test
        @DisplayName("포스트 삭제 성공")
        void successDeletePost() throws Exception {
            Integer postId = 1;

            given(postService.deletePostById(any(Integer.class), any(String.class), any(String.class))).willReturn(new PostRes("포스트 삭제 완료", postId));

            mockMvc.perform(delete(String.format("/api/v1/posts/%d", postId))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                    .andExpect(jsonPath("$.result.message").value("포스트 삭제 완료"))
                    .andExpect(jsonPath("$.result.postId").value(postId));
        }

        @Test
        @WithAnonymousUser
        @DisplayName("포스트 삭제 실패 - 인증 실패")
        void authenticationFailed() throws Exception {
            UserEntity user = UserEntity.builder()
                    .userName("Soyeong")
                    .password("12345")
                    .build();
            Integer postId = 1;

            given(postService.deletePostById(any(Integer.class), any(String.class), any(String.class))).willThrow(new HealingSnsAppException(ErrorCode.USERNAME_NOT_FOUND, String.format("%s은(는) 없는 회원입니다.", user.getUsername())));

            mockMvc.perform(delete(String.format("/api/v1/posts/%d", postId))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("포스트 삭제 실패 - 작성자 불일치")
        void mismatchedAuthorAndUser() throws Exception {
            Integer postId = 1;

            given(postService.deletePostById(any(Integer.class), any(String.class), any(String.class))).willThrow(new HealingSnsAppException(ErrorCode.INVALID_PERMISSION, "사용자가 권한이 없습니다."));

            mockMvc.perform(delete(String.format("/api/v1/posts/%d", postId))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.resultCode").value("ERROR"))
                    .andExpect(jsonPath("$.result.errorCode").value("INVALID_PERMISSION"))
                    .andExpect(jsonPath("$.result.message").value("사용자가 권한이 없습니다."));
        }

        @Test
        @DisplayName("포스트 삭제 실패 - 데이터베이스 에러")
        void notFoundDatabase() throws Exception {
            Integer postId = 1;

            given(postService.deletePostById(any(Integer.class), any(String.class), any(String.class))).willThrow(new HealingSnsAppException(ErrorCode.DATABASE_ERROR, "DB에러"));

            mockMvc.perform(delete(String.format("/api/v1/posts/%d", postId))
                            .with(csrf()))
                    .andDo(print())
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.resultCode").value("ERROR"))
                    .andExpect(jsonPath("$.result.errorCode").value("DATABASE_ERROR"))
                    .andExpect(jsonPath("$.result.message").value("DB에러"));
        }
    }

    @Nested
    @DisplayName("마이피드 조회 테스트")
    class GetMyFeed {

        @Test
        @DisplayName("마이피드 조회 성공")
        void successGetMyFeed() throws Exception {
    //        PageRequest pageRequest = PageRequest.of(1, 10, Sort.by("createdAt").descending());
    //
    //        List<PostViewRes> postList = new ArrayList<>();
    //        for (int i = 0; i < 10; i++) {
    //            LocalDateTime now = LocalDateTime.now().plusMinutes(i);
    //            postList.add(new PostViewRes(i, "title"+i, "body"+i, "user", now, now));
    //        }
    //
    //        int start = (int) pageRequest.getOffset();
    //        int end = Math.min((start + pageRequest.getPageSize()), postList.size());
    //
    //        Page<PostViewRes> postPage = new PageImpl<>(postList.subList(start, end), pageRequest, postList.size());
    //
    //        given(postService.getMyFeed(any(Pageable.class), any(String.class))).willReturn(postPage);
    //
    //        mockMvc.perform(get("/api/v1/posts/my")
    //                        .param("page", "0")
    //                        .param("size", "10")
    //                        .param("sort", "createdAt,desc")
    //                        .param("userName", "user"))
    //                .andDo(print())
    //                .andExpect(status().isOk());
    //
    //        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
    //        verify(postService).getPostList(pageableCaptor.capture());
    //        PageRequest pageable = (PageRequest) pageableCaptor.getValue();
    //
    //        assertEquals(0, pageable.getPageNumber());
    //        assertEquals(10, pageable.getPageSize());
    //        assertEquals(Sort.by("createdAt", "desc"), pageable.withSort(Sort.by("createdAt", "desc")).getSort());

            given(postService.getMyFeed(any(Pageable.class), any(String.class))).willReturn(Page.empty());

            mockMvc.perform(get("/api/v1/posts/my")
                            .param("page", "0")
                            .param("size", "10")
                            .param("sort", "createdAt,desc")
                            .param("userName", "user"))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        @WithAnonymousUser
        @DisplayName("마이피드 조회 실패 - 로그인 하지 않은 경우")
        void NotLogin() throws Exception {
            given(postService.getMyFeed(any(Pageable.class), any(String.class))).willReturn(Page.empty());

            mockMvc.perform(get("/api/v1/posts/my")
                            .param("page", "0")
                            .param("size", "10")
                            .param("sort", "createdAt,desc")
                            .param("userName", "user"))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        }
    }


    @Nested
    @DisplayName("좋아요 +1 테스트")
    class IncreaseLikeTest {

        @Test
        @DisplayName("좋아요 누르기 성공")
        void increaseLike() throws Exception {
            TestInfoFixture.TestInfo fixture = TestInfoFixture.get();

            doNothing().when(postService).increaseLike(fixture.getPostId(), fixture.getUserName());

            mockMvc.perform(post(String.format("/api/v1/posts/%d/likes", fixture.getPostId()))
                   .with(csrf())
                   .contentType(MediaType.APPLICATION_JSON))
                   .andDo(print())
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.result").value("좋아요를 눌렀습니다."));
        }

        @Test
        @WithAnonymousUser
        @DisplayName("좋아요 누르기 실패 - 로그인 하지 않은 경우")
        void NotLogin() throws Exception {
            TestInfoFixture.TestInfo fixture = TestInfoFixture.get();

            doNothing().when(postService).increaseLike(any(Integer.class), any(String.class));

            mockMvc.perform(post(String.format("/api/v1/posts/%d/likes", fixture.getPostId()))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());

            verify(postService, never()).increaseLike(any(Integer.class), any(String.class));
        }

        @Test
        @DisplayName("좋아요 누르기 실패 - 포스트가 없는 경우")
        void NotFoundPost () throws Exception {
            TestInfoFixture.TestInfo fixture = TestInfoFixture.get();

            doThrow(new HealingSnsAppException(ErrorCode.POST_NOT_FOUND, "해당 포스트가 없습니다."))
                    .when(postService).increaseLike(fixture.getPostId(), fixture.getUserName());

            mockMvc.perform(post(String.format("/api/v1/posts/%d/likes", fixture.getPostId()))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.resultCode").value("ERROR"))
                    .andExpect(jsonPath("$.result.errorCode").value("POST_NOT_FOUND"))
                    .andExpect(jsonPath("$.result.message").value("해당 포스트가 없습니다."));
        }

        @Test
        @DisplayName("좋아요 누르기 실패 - 현재 로그인한 회원이 존재하지 않는 경우")
        void NotFoundUser() throws Exception {
            TestInfoFixture.TestInfo fixture = TestInfoFixture.get();

            doThrow(new HealingSnsAppException(ErrorCode.USERNAME_NOT_FOUND, String.format("%s은(는) 없는 회원입니다.", fixture.getUserName())))
                    .when(postService).increaseLike(fixture.getPostId(), fixture.getUserName());

            mockMvc.perform(post(String.format("/api/v1/posts/%d/likes", fixture.getPostId()))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.resultCode").value("ERROR"))
                    .andExpect(jsonPath("$.result.errorCode").value("USERNAME_NOT_FOUND"))
                    .andExpect(jsonPath("$.result.message").value(String.format("%s은(는) 없는 회원입니다.", fixture.getUserName())));
        }
    }

    @Nested
    @DisplayName("좋아요 취소 테스트")
    class DecreaseLikeTest {

        @Test
        @DisplayName("좋아요 취소 성공")
        void decreaseLike() throws Exception {
            TestInfoFixture.TestInfo fixture = TestInfoFixture.get();

            doNothing().when(postService).decreaseLike(fixture.getPostId(), fixture.getUserName());

            mockMvc.perform(delete(String.format("/api/v1/posts/%d/likes", fixture.getPostId()))
                   .with(csrf())
                   .contentType(MediaType.APPLICATION_JSON))
                   .andDo(print())
                   .andExpect(status().isOk())
                   .andExpect(jsonPath("$.result").value("좋아요를 취소했습니다."));
        }

        @Test
        @WithAnonymousUser
        @DisplayName("좋아요 취소 실패 - 로그인 하지 않은 경우")
        void NotLogin() throws Exception {
            TestInfoFixture.TestInfo fixture = TestInfoFixture.get();

            doNothing().when(postService).decreaseLike(any(Integer.class), any(String.class));

            mockMvc.perform(delete(String.format("/api/v1/posts/%d/likes", fixture.getPostId()))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());

            verify(postService, never()).increaseLike(any(Integer.class), any(String.class));
        }

        @Test
        @DisplayName("좋아요 취소 실패 - 포스트가 없는 경우")
        void NotFoundPost () throws Exception {
            TestInfoFixture.TestInfo fixture = TestInfoFixture.get();

            doThrow(new HealingSnsAppException(ErrorCode.POST_NOT_FOUND, "해당 포스트가 없습니다."))
                    .when(postService).decreaseLike(fixture.getPostId(), fixture.getUserName());

            mockMvc.perform(delete(String.format("/api/v1/posts/%d/likes", fixture.getPostId()))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.resultCode").value("ERROR"))
                    .andExpect(jsonPath("$.result.errorCode").value("POST_NOT_FOUND"))
                    .andExpect(jsonPath("$.result.message").value("해당 포스트가 없습니다."));
        }

        @Test
        @DisplayName("좋아요 취소 실패 - 현재 로그인한 회원이 존재하지 않는 경우")
        void NotFoundUser() throws Exception {
            TestInfoFixture.TestInfo fixture = TestInfoFixture.get();

            doThrow(new HealingSnsAppException(ErrorCode.USERNAME_NOT_FOUND, String.format("%s은(는) 없는 회원입니다.", fixture.getUserName())))
                    .when(postService).decreaseLike(fixture.getPostId(), fixture.getUserName());

            mockMvc.perform(delete(String.format("/api/v1/posts/%d/likes", fixture.getPostId()))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.resultCode").value("ERROR"))
                    .andExpect(jsonPath("$.result.errorCode").value("USERNAME_NOT_FOUND"))
                    .andExpect(jsonPath("$.result.message").value(String.format("%s은(는) 없는 회원입니다.", fixture.getUserName())));
        }
    }
}