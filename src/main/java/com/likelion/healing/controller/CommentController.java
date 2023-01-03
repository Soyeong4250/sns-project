package com.likelion.healing.controller;

import com.likelion.healing.domain.dto.CommentAddRes;
import com.likelion.healing.domain.dto.CommentReq;
import com.likelion.healing.domain.entity.CommentEntity;
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
    public Response<CommentAddRes> addComment(@PathVariable Integer postId, @Valid @RequestBody CommentReq commentReq, Authentication authentication) {
        log.info("comment : {}", commentReq.getComment());
        String userName = authentication.getName();
        CommentEntity savedComment = commentService.addCommentByPostId(postId, commentReq, userName);
        return Response.success(CommentAddRes.of(savedComment));
    }
}
