package com.likelion.healing.controller;

import com.likelion.healing.domain.dto.CommentReq;
import com.likelion.healing.domain.dto.CommentRes;
import com.likelion.healing.domain.entity.Response;
import com.likelion.healing.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

    @GetMapping
    public Response<Page<CommentRes>> getCommentList(@PathVariable Integer postId, @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        log.debug("getCommentList() 실행");
        return Response.success(commentService.getCommentList(postId, pageable));
    }

    @PutMapping("{id}")
    public Response<CommentRes> updateComment(@PathVariable(name = "postId") Integer postId, @PathVariable(name = "id") Integer commentId, @Valid @RequestBody CommentReq commentReq, Authentication authentication) {
        log.debug("updateComment() 실행");
        return Response.success(commentService.updateComment(postId, commentId, commentReq, authentication.getName()));
    }
}
