package com.likelion.healing.controller;

import com.likelion.healing.domain.dto.PostReq;
import com.likelion.healing.domain.dto.PostRes;
import com.likelion.healing.domain.dto.PostViewRes;
import com.likelion.healing.domain.entity.Response;
import com.likelion.healing.service.PostService;
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

import java.sql.SQLException;

@RestController
@Slf4j
@RequiredArgsConstructor
@Api(tags = {"02. PostController"})
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;

    @ApiOperation(value = "포스트 등록", notes = "등록할 포스트 정보를 입력받아 포스트 등록 성공유무를 반환")
    @ApiResponses({
            @ApiResponse(code = 200, message = "포스트 등록 성공"),
            @ApiResponse(code = 401, message = "Jwt Token이 올바른 형태가 아니거나 만료된 경우 👉 INVALID_PERMISSION, 에러 메세지 반환"),
            @ApiResponse(code = 500, message = "데이터베이스 예외가 발생한 경우 👉 DATABASE_ERROR, 에러 메세지 반환")
    })
    @PostMapping()
    public Response<PostRes> addPost(@RequestBody PostReq postReq, Authentication authentication) throws SQLException  {
        log.info("title : {}, body : {}", postReq.getTitle(), postReq.getBody());
        String userName = authentication.getName();
        log.info("authentication : {}", authentication);
        log.info("userName : {}", userName);
        PostRes postRes = postService.addPost(postReq, userName);
        return Response.success(postRes);
    }

    @ApiOperation(value = "포스트 목록 조회", notes = "포스트 목록 조회 성공유무를 반환")
    @ApiResponses({
            @ApiResponse(code = 200, message = "포스트 목록 조회 성공"),
            @ApiResponse(code = 500, message = "데이터베이스 예외가 발생한 경우 👉 DATABASE_ERROR, 에러 메세지 반환")
    })
    @GetMapping()
    public Response<Page<PostViewRes>> getPostList(@PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) throws SQLException {
        log.debug("getPostList() 실행");
        return Response.success(postService.getPostList(pageable));
    }

    @ApiOperation(value = "포스트 상세 조회", notes = "postId를 PathVariable로 받아 포스트 상세 조회 성공유무를 반환")
    @ApiResponses({
            @ApiResponse(code = 200, message = "포스트 상세 조회 성공"),
            @ApiResponse(code = 500, message = "데이터베이스 예외가 발생한 경우 👉 DATABASE_ERROR, 에러 메세지 반환")
    })
    @GetMapping("/{id}")
    public Response<PostViewRes> getPostById(@PathVariable Integer id) throws SQLException {
        log.info("postId : {}", id);
        PostViewRes postRes = postService.getPostById(id);
        return Response.success(postRes);
    }

    @ApiOperation(value = "포스트 수정", notes = "id - PathVariable, 수정할 포스트 - RequestBody, Token - Authorization을 입력받아 포스트 수정 성공유무 반환")
    @ApiResponses({
            @ApiResponse(code = 200, message = "포스트 수정 성공"),
            @ApiResponse(code = 401, message = "현재 로그인한 회원을 찾지못하는 경우, 작성자와 현재 로그인한 회원이 불일치한 경우 👉 INVALID_PERMISSION, 에러 메세지 반환"),
            @ApiResponse(code = 500, message = "데이터베이스 예외가 발생한 경우 👉 DATABASE_ERROR, 에러 메세지 반환"),
    })
    @PutMapping("/{id}")
    public Response<PostRes> updatePostById(@PathVariable Integer id, @RequestBody PostReq postEditReq, Authentication authentication) throws SQLException {
        log.info("postId : {}", id);
        log.info("post title : {}", postEditReq.getTitle());
        log.info("post body : {}", postEditReq.getBody());
        PostRes postRes = postService.updatePostById(id, postEditReq, authentication.getName(), authentication.getAuthorities().iterator().next().getAuthority());
        return Response.success(postRes);
    }

    @ApiOperation(value = "포스트 삭제", notes = "id - PathVariable, Token - Authorization을 입력받아 포스트 삭제 성공유무 반환")
    @ApiResponses({
            @ApiResponse(code = 200, message = "포스트 삭제 성공"),
            @ApiResponse(code = 401, message = "현재 로그인한 회원을 찾지못하는 경우, 작성자와 현재 로그인한 회원이 불일치한 경우 👉 INVALID_PERMISSION, 에러 메세지 반환"),
            @ApiResponse(code = 500, message = "데이터베이스 예외가 발생한 경우 👉 DATABASE_ERROR, 에러 메세지 반환"),
    })
    @DeleteMapping("/{id}")
    public Response<PostRes> deletePostById(@PathVariable Integer id, Authentication authentication) throws SQLException {
        log.info("postId : {}", id);
        PostRes postRes = postService.deletePostById(id, authentication.getName(), authentication.getAuthorities().iterator().next().getAuthority());
        return Response.success(postRes);
    }
}
