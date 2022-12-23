package com.likelion.healing.controller;

import com.likelion.healing.domain.dto.PostReq;
import com.likelion.healing.domain.dto.PostRes;
import com.likelion.healing.domain.dto.PostViewRes;
import com.likelion.healing.domain.entity.Response;
import com.likelion.healing.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;

    @PostMapping()
    public Response<PostRes> addPost(@RequestBody PostReq postReq, Authentication authentication) throws SQLException  {
        log.info("title : {}, body : {}", postReq.getTitle(), postReq.getBody());
        String userName = authentication.getName();
        log.info("authentication : {}", authentication);
        log.info("userName : {}", userName);
        PostRes postRes = postService.addPost(postReq, userName);
        return Response.success(new PostRes(postRes.getMessage(), postRes.getPostId()));
    }

    @GetMapping()
    public Response<Page<PostViewRes>> getPostList(@PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) throws SQLException {
        log.debug("getPostList() 실행");
        return Response.success(postService.getPostList(pageable));
    }

    @GetMapping("/{postId}")
    public Response<PostViewRes> getPostById(@PathVariable Integer postId) throws SQLException {
        log.info("postId : {}", postId);
        PostViewRes postRes = postService.getPostById(postId);
        return Response.success(new PostViewRes(postRes.getId(), postRes.getTitle(), postRes.getBody(), postRes.getUserName(), postRes.getCreatedAt(), postRes.getLastModifiedAt()));
    }

    @PutMapping("/{id}")
    public Response<PostRes> updatePostById(@PathVariable Integer id, @RequestBody PostReq postEditReq, Authentication authentication) throws SQLException {
        log.info("postId : {}", id);
        log.info("post title : {}", postEditReq.getTitle());
        log.info("post body : {}", postEditReq.getBody());
        PostRes postRes = postService.updatePostById(id, postEditReq, authentication.getName());
        return Response.success(postRes);
    }

    @DeleteMapping("/{id}")
    public Response<PostRes> deletePostById(@PathVariable Integer id, Authentication authentication) {
        log.info("postId : {}", id);
        PostRes postRes = postService.deletePostById(id, authentication.getName());
        return Response.success(postRes);
    }
}
