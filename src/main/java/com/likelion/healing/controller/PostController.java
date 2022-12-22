package com.likelion.healing.controller;

import com.likelion.healing.domain.dto.PostAddReq;
import com.likelion.healing.domain.dto.PostAddRes;
import com.likelion.healing.domain.entity.Response;
import com.likelion.healing.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;

    @PostMapping()
    public Response<PostAddRes> addPost(@RequestBody PostAddReq postAddReq, Authentication authentication) {
        log.info("title : {}, body : {}", postAddReq.getTitle(), postAddReq.getBody());
        String userName = authentication.getName();
        log.info("userName : {}", userName);
        PostAddRes postAddRes = postService.addPost(postAddReq, userName);
        return Response.success(new PostAddRes(postAddRes.getMessage(), postAddRes.getPostId()));
    }
}
