package com.likelion.healing.controller;

import com.likelion.healing.domain.dto.CommentDeleteRes;
import com.likelion.healing.domain.dto.CommentReq;
import com.likelion.healing.domain.dto.CommentRes;
import com.likelion.healing.service.CommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.SQLException;

@RestController
@RequiredArgsConstructor
@Slf4j
@Api(tags = {"03. CommentController"})
@RequestMapping("/api/v1/posts/{postId}/comments")
public class CommentController {

    private final CommentService commentService;

    @ApiOperation(value = "댓글 등록", notes = "등록할 포스트 정보를 입력받아 포스트 등록 성공유무를 반환")
    @ApiResponses({
            @ApiResponse(code = 200, message = "댓글 등록 성공"),
            @ApiResponse(code = 401, message = "Jwt Token이 올바른 형태가 아니거나 만료된 경우 👉 INVALID_PERMISSION, 에러 메세지 반환"),
            @ApiResponse(code = 404, message = "포스트를 찾지 못하는 경우 👉 POST_NOT_FOUND, 에러 메세지 반환"),
            @ApiResponse(code = 500, message = "데이터베이스 예외가 발생한 경우 👉 DATABASE_ERROR, 에러 메세지 반환")
    })

    @PostMapping
    public Response<CommentRes> createComment(@PathVariable Integer postId, @Valid @RequestBody CommentReq commentReq, Authentication authentication) throws SQLException {
        log.debug("createComment() 실행");
        log.info("comment : {}", commentReq.getComment());
        String userName = authentication.getName();
        return Response.success(commentService.createComment(postId, commentReq, userName));
    }

    @ApiOperation(value = "댓글 목록 조회", notes = "댓글 목록 조회 성공유무를 반환")
    @ApiResponses({
            @ApiResponse(code = 200, message = "댓글 목록 조회 성공"),
            @ApiResponse(code = 404, message = "포스트를 찾지 못하는 경우 👉 POST_NOT_FOUND, 에러 메세지 반환"),
            @ApiResponse(code = 500, message = "데이터베이스 예외가 발생한 경우 👉 DATABASE_ERROR, 에러 메세지 반환"),
    })
    @GetMapping
    public Response<Page<CommentRes>> getCommentList(@PathVariable Integer postId, @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) throws SQLException {
        log.debug("getCommentList() 실행");
        return Response.success(commentService.getCommentList(postId, pageable));
    }

    @ApiOperation(value = "댓글 수정", notes = "postId, commentId, Token을 입력받아 댓글 수정 성공유무 반환")
    @ApiResponses({
            @ApiResponse(code = 200, message = "댓글 수정 성공"),
            @ApiResponse(code = 401, message = "작성자와 현재 로그인한 회원이 불일치한 경우 👉 INVALID_PERMISSION, 에러 메세지 반환"),
            @ApiResponse(code = 404, message = "포스트를 찾지 못하는 경우 👉 POST_NOT_FOUND, 에러 메세지 반환"),
            @ApiResponse(code = 404, message = "현재 로그인한 회원을 찾지 못하는 경우 👉 USERNAME_NOT_FOUND, 에러 메세지 반환"),
            @ApiResponse(code = 404, message = "댓글을 찾지 못하는 경우 👉 COMMENT_NOT_FOUND, 에러 메세지 반환"),
            @ApiResponse(code = 500, message = "데이터베이스 예외가 발생한 경우 👉 DATABASE_ERROR, 에러 메세지 반환"),
    })
    @PutMapping("/{id}")
    public Response<CommentRes> updateComment(@PathVariable(name = "postId") Integer postId, @PathVariable(name = "id") Integer commentId, @Valid @RequestBody CommentReq commentReq, Authentication authentication) throws SQLException {
        log.debug("updateComment() 실행");
        return Response.success(commentService.updateComment(postId, commentId, commentReq, authentication.getName()));
    }

    @ApiOperation(value = "댓글 삭제", notes = "postId, commentId, Token을 입력받아 댓글 삭제 성공유무 반환")
    @ApiResponses({
            @ApiResponse(code = 200, message = "댓글 삭제 성공"),
            @ApiResponse(code = 401, message = "작성자와 현재 로그인한 회원이 불일치한 경우 👉 INVALID_PERMISSION, 에러 메세지 반환"),
            @ApiResponse(code = 404, message = "포스트를 찾지 못하는 경우 👉 POST_NOT_FOUND, 에러 메세지 반환"),
            @ApiResponse(code = 404, message = "현재 로그인한 회원을 찾지 못하는 경우 👉 USERNAME_NOT_FOUND, 에러 메세지 반환"),
            @ApiResponse(code = 404, message = "댓글을 찾지 못하는 경우 👉 COMMENT_NOT_FOUND, 에러 메세지 반환"),
            @ApiResponse(code = 500, message = "데이터베이스 예외가 발생한 경우 👉 DATABASE_ERROR, 에러 메세지 반환"),
    })
    @DeleteMapping("/{id}")
    public Response<CommentDeleteRes> deleteComment(@PathVariable(name = "postId") Integer postId, @PathVariable(name = "id") Integer commentId, Authentication authentication) throws SQLException {
        log.debug("deleteComment() 실행");
        return Response.success(commentService.deleteComment(postId, commentId, authentication.getName()));
    }
}
