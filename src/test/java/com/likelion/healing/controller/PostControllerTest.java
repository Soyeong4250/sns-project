package com.likelion.healing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.healing.domain.dto.PostReq;
import com.likelion.healing.domain.dto.PostRes;
import com.likelion.healing.domain.dto.PostViewRes;
import com.likelion.healing.exception.ErrorCode;
import com.likelion.healing.exception.HealingSnsAppException;
import com.likelion.healing.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Timestamp;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

        given(postService.addPost(any(PostReq.class), "Bearerrrr " + any(String.class))).willThrow(new HealingSnsAppException(ErrorCode.INVALID_PERMISSION, "사용자가 권한이 없습니다."));

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

    /*@Test
    @DisplayName("포스트 전체 목록 - 생성일자 내림차순 조회 테스트")
    void getPostList() throws Exception {
        List<PostViewRes> postList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            postList.add(new PostViewRes(i, "title" + i, "body" + i, "Soyeong", new Timestamp(System.currentTimeMillis())));
        }
        postList.stream().sorted(Comparator.comparing(PostViewRes::getCreatedAt).reversed());
        Page<PostViewRes> postPage = listToPage(postList);

        Mockito.when(postService.getPostList(any(Pageable.class))).thenReturn(postPage);


        Response<Page<PostViewRes>> = postController.getPostList(Pageable);
        Assertions.assertTrue(jsonPath("$.result.content[0].createdAt").comparejsonPath("$.result.content[1].createdAt"));
    }

    private Page<PostViewRes> listToPage(List<PostViewRes> postList) {
        PageRequest pageRequest = PageRequest.of(1, 20);
        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), postList.size());
        return new PageImpl<>(postList.subList(start, end), pageRequest, postList.size());
    }*/

    @Test
    @WithMockUser
    @DisplayName("포스트 단건 조회 성공")
    void successfulGetPostById() throws Exception {
        Integer postId = 1;
        Timestamp now = new Timestamp(System.currentTimeMillis());
        PostViewRes post = PostViewRes.builder()
                .id(1)
                .title("title1")
                .body("body1")
                .userName("Soyeong")
                .createdAt(now)
                .lastModifiedAt(now)
                .build();

        given(postService.getPostById(any(Integer.class))).willReturn(post);

        mockMvc.perform(get(String.format("/api/v1/posts/%d", postId))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(postId)))
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
}