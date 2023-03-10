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
@WithMockUser(username = "userName")
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    PostService postService;

    @Autowired
    ObjectMapper objectMapper;

    @Nested
    @DisplayName("????????? ?????? ?????????")
    class CreatePostTest{

        @Test
        @DisplayName("????????? ?????? ??????")
        void successCreatePost() throws Exception {
            PostReq req = PostReq.builder()
                    .title("title1")
                    .body("body1")
                    .build();

            given(postService.createPost(any(PostReq.class), "Bearer " + any(String.class))).willReturn(new PostRes("????????? ?????? ??????", 1));

            mockMvc.perform(post("/api/v1/posts")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(req)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                    .andExpect(jsonPath("$.result.message").value("????????? ?????? ??????"))
                    .andExpect(jsonPath("$.result.postId").value(1));
        }

        @Test
        @WithAnonymousUser
        @DisplayName("????????? ?????? ?????? - JWT??? Bearer Token?????? ????????? ?????? ??????")
        void notStartsWithBearer() throws Exception {
            PostReq req = PostReq.builder()
                    .title("title1")
                    .body("body1")
                    .build();

            given(postService.createPost(any(PostReq.class), any(String.class))).willThrow(new HealingSnsAppException(ErrorCode.INVALID_PERMISSION, "???????????? ????????? ????????????."));

            mockMvc.perform(post("/api/v1/posts")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(req)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @WithAnonymousUser
        @DisplayName("????????? ?????? ?????? - JWT??? ???????????? ?????? ??????")
        void expiredToken() throws Exception {
            PostReq req = PostReq.builder()
                    .title("title1")
                    .body("body1")
                    .build();

            given(postService.createPost(any(PostReq.class), "Bearer " + any(String.class))).willThrow(new HealingSnsAppException(ErrorCode.INVALID_PERMISSION, "???????????? ????????? ????????????."));

            mockMvc.perform(post("/api/v1/posts")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(req)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        }
    }

    @Test
    @DisplayName("????????? ?????? ?????? ?????? - ???????????? ????????????")
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
    @DisplayName("????????? ?????? ?????? ??????")
    void successGetPostById() throws Exception {
        Integer postId = 1;
        PostViewRes post = PostViewRes.builder()
                .id(1)
                .title("title1")
                .body("body1")
                .userName("userName")
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
                .andExpect(jsonPath("$.result.userName").value("userName"))
                .andExpect(jsonPath("$.result.createdAt").exists())
                .andExpect(jsonPath("$.result.lastModifiedAt").exists());
    }

    @Nested
    @DisplayName("????????? ?????? ?????????")
    class UpdatePostTest {

        @Test
        @WithAnonymousUser
        @DisplayName("????????? ?????? ?????? - ?????? ??????")
        void authenticationFailed() throws Exception {
            PostReq req = PostReq.builder()
                    .title("test title")
                    .body("test body")
                    .build();
            UserEntity user = UserEntity.builder()
                    .userName("userName")
                    .password("12345")
                    .build();
            Integer postId = 1;

            given(postService.updatePostById(any(Integer.class), any(PostReq.class), any(String.class))).willThrow(new HealingSnsAppException(ErrorCode.USERNAME_NOT_FOUND, String.format("%s???(???) ?????? ???????????????.", user.getUserName())));

            mockMvc.perform(put(String.format("/api/v1/posts/%d", postId))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(req)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("????????? ?????? ?????? - ????????? ?????????")
        void mismatchedAuthorAndUser() throws Exception {
            PostReq req = PostReq.builder()
                    .title("test title")
                    .body("test body")
                    .build();
            Integer postId = 1;

            given(postService.updatePostById(any(Integer.class), any(PostReq.class), any(String.class))).willThrow(new HealingSnsAppException(ErrorCode.INVALID_PERMISSION, "???????????? ????????? ????????????."));

            mockMvc.perform(put(String.format("/api/v1/posts/%d", postId))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(req)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.resultCode").value("ERROR"))
                    .andExpect(jsonPath("$.result.errorCode").value("INVALID_PERMISSION"))
                    .andExpect(jsonPath("$.result.message").value("???????????? ????????? ????????????."));
        }

        @Test
        @DisplayName("????????? ?????? ?????? - ?????????????????? ??????")
        void notFoundDatabase() throws Exception {
            PostReq req = PostReq.builder()
                    .title("test title")
                    .body("test body")
                    .build();
            Integer postId = 1;

            given(postService.updatePostById(any(Integer.class), any(PostReq.class), any(String.class))).willThrow(new HealingSnsAppException(ErrorCode.DATABASE_ERROR, "DB ??????"));

            mockMvc.perform(put(String.format("/api/v1/posts/%d", postId))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(req)))
                    .andDo(print())
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.resultCode").value("ERROR"))
                    .andExpect(jsonPath("$.result.errorCode").value("DATABASE_ERROR"))
                    .andExpect(jsonPath("$.result.message").value("DB ??????"));
        }

        @Test
        @DisplayName("????????? ?????? ??????")
        void successUpdatePost() throws Exception {
            PostReq req = PostReq.builder()
                    .title("test title")
                    .body("test body")
                    .build();
            Integer postId = 1;

            given(postService.updatePostById(any(Integer.class), any(PostReq.class), any(String.class))).willReturn(new PostRes("????????? ?????? ??????", postId));

            mockMvc.perform(put(String.format("/api/v1/posts/%d", postId))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(req)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                    .andExpect(jsonPath("$.result.message").value("????????? ?????? ??????"))
                    .andExpect(jsonPath("$.result.postId").value(postId));
        }
    }


    @Nested
    @DisplayName("????????? ?????? ?????????")
    class DeletePostTest {

        @Test
        @DisplayName("????????? ?????? ??????")
        void successDeletePost() throws Exception {
            Integer postId = 1;

            given(postService.deletePostById(any(Integer.class), any(String.class))).willReturn(new PostRes("????????? ?????? ??????", postId));

            mockMvc.perform(delete(String.format("/api/v1/posts/%d", postId))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                    .andExpect(jsonPath("$.result.message").value("????????? ?????? ??????"))
                    .andExpect(jsonPath("$.result.postId").value(postId));
        }

        @Test
        @WithAnonymousUser
        @DisplayName("????????? ?????? ?????? - ?????? ??????")
        void authenticationFailed() throws Exception {
            UserEntity user = UserEntity.builder()
                    .userName("userName")
                    .password("12345")
                    .build();
            Integer postId = 1;

            given(postService.deletePostById(any(Integer.class), any(String.class))).willThrow(new HealingSnsAppException(ErrorCode.USERNAME_NOT_FOUND, String.format("%s???(???) ?????? ???????????????.", user.getUserName())));

            mockMvc.perform(delete(String.format("/api/v1/posts/%d", postId))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("????????? ?????? ?????? - ????????? ?????????")
        void mismatchedAuthorAndUser() throws Exception {
            Integer postId = 1;

            given(postService.deletePostById(any(Integer.class), any(String.class))).willThrow(new HealingSnsAppException(ErrorCode.INVALID_PERMISSION, "???????????? ????????? ????????????."));

            mockMvc.perform(delete(String.format("/api/v1/posts/%d", postId))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.resultCode").value("ERROR"))
                    .andExpect(jsonPath("$.result.errorCode").value("INVALID_PERMISSION"))
                    .andExpect(jsonPath("$.result.message").value("???????????? ????????? ????????????."));
        }

        @Test
        @DisplayName("????????? ?????? ?????? - ?????????????????? ??????")
        void notFoundDatabase() throws Exception {
            Integer postId = 1;

            given(postService.deletePostById(any(Integer.class), any(String.class))).willThrow(new HealingSnsAppException(ErrorCode.DATABASE_ERROR, "DB ??????"));

            mockMvc.perform(delete(String.format("/api/v1/posts/%d", postId))
                            .with(csrf()))
                    .andDo(print())
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.resultCode").value("ERROR"))
                    .andExpect(jsonPath("$.result.errorCode").value("DATABASE_ERROR"))
                    .andExpect(jsonPath("$.result.message").value("DB ??????"));
        }
    }

    @Nested
    @DisplayName("???????????? ?????? ?????????")
    class GetMyFeed {

        @Test
        @DisplayName("???????????? ?????? ??????")
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
        @DisplayName("???????????? ?????? ?????? - ????????? ?????? ?????? ??????")
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
    @DisplayName("????????? ?????????")
    class IncreaseLikeTest {

        @Test
        @DisplayName("????????? ????????? ??????")
        void increaseLike() throws Exception {
            TestInfoFixture.TestInfo fixture = TestInfoFixture.get();

            given(postService.pushLike(any(Integer.class), any(String.class))).willReturn("???????????? ???????????????.");

            mockMvc.perform(post(String.format("/api/v1/posts/%d/likes", fixture.getPostId()))
                   .with(csrf())
                   .contentType(MediaType.APPLICATION_JSON))
                   .andDo(print())
                   .andExpect(status().isOk())
                    .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                   .andExpect(jsonPath("$.result").value("???????????? ???????????????."));
        }

        @Test
        @WithAnonymousUser
        @DisplayName("????????? ????????? ?????? - ????????? ?????? ?????? ??????")
        void NotLogin() throws Exception {
            TestInfoFixture.TestInfo fixture = TestInfoFixture.get();

            mockMvc.perform(post(String.format("/api/v1/posts/%d/likes", fixture.getPostId()))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());

            verify(postService, never()).pushLike(any(Integer.class), any(String.class));
        }

        @Test
        @DisplayName("????????? ????????? ?????? - ???????????? ?????? ??????")
        void NotFoundPost () throws Exception {
            TestInfoFixture.TestInfo fixture = TestInfoFixture.get();

            given(postService.pushLike(any(Integer.class), any(String.class)))
                    .willThrow(new HealingSnsAppException(ErrorCode.POST_NOT_FOUND, "?????? ???????????? ????????????."));

            mockMvc.perform(post(String.format("/api/v1/posts/%d/likes", fixture.getPostId()))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.resultCode").value("ERROR"))
                    .andExpect(jsonPath("$.result.errorCode").value("POST_NOT_FOUND"))
                    .andExpect(jsonPath("$.result.message").value("?????? ???????????? ????????????."));
        }

        @Test
        @DisplayName("????????? ????????? ?????? - ?????? ???????????? ????????? ???????????? ?????? ??????")
        void NotFoundUser() throws Exception {
            TestInfoFixture.TestInfo fixture = TestInfoFixture.get();

            doThrow(new HealingSnsAppException(ErrorCode.USERNAME_NOT_FOUND, String.format("%s???(???) ?????? ???????????????.", fixture.getUserName())))
                    .when(postService).pushLike(fixture.getPostId(), fixture.getUserName());

            mockMvc.perform(post(String.format("/api/v1/posts/%d/likes", fixture.getPostId()))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.resultCode").value("ERROR"))
                    .andExpect(jsonPath("$.result.errorCode").value("USERNAME_NOT_FOUND"))
                    .andExpect(jsonPath("$.result.message").value(String.format("%s???(???) ?????? ???????????????.", fixture.getUserName())));
        }

        @Test
        @DisplayName("????????? ?????? ??????")
        void decreaseLike() throws Exception {
            TestInfoFixture.TestInfo fixture = TestInfoFixture.get();

            given(postService.pushLike(any(Integer.class), any(String.class))).willReturn("???????????? ??????????????????.");

            mockMvc.perform(post(String.format("/api/v1/posts/%d/likes", fixture.getPostId()))
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                    .andExpect(jsonPath("$.result").value("???????????? ??????????????????."));
        }
    }

    @Test
    @DisplayName("????????? ?????? ??????")
    void countLikesTest() throws Exception {
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();

        given(postService.countLike(any(Integer.class))).willReturn(5);

        mockMvc.perform(get(String.format("/api/v1/posts/%d/likes", fixture.getPostId()))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result").value(5));
    }
}