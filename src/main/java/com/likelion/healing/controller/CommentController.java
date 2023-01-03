package com.likelion.healing.controller;

import com.likelion.healing.domain.dto.CommentReq;
import com.likelion.healing.domain.dto.CommentRes;
import com.likelion.healing.domain.entity.Response;
import com.likelion.healing.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/posts/{postId}/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public Response<CommentRes> createComment(@PathVariable Integer postId, @Valid @RequestBody CommentReq commentReq, Authentication authentication) {
        log.debug("createComment() 실행");
        log.info("comment : {}", commentReq.getComment());
        String userName = authentication.getName();
        return Response.success(commentService.createComment(postId, commentReq, userName));
    }
}
