package com.likelion.healing.service;

import com.likelion.healing.domain.dto.PostReq;
import com.likelion.healing.domain.dto.PostRes;
import com.likelion.healing.domain.dto.PostViewRes;
import com.likelion.healing.domain.entity.PostEntity;
import com.likelion.healing.domain.entity.UserEntity;
import com.likelion.healing.domain.entity.UserRole;
import com.likelion.healing.exception.ErrorCode;
import com.likelion.healing.exception.HealingSnsAppException;
import com.likelion.healing.repository.PostRepository;
import com.likelion.healing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public PostRes addPost(PostReq postReq, String userName) {
        UserEntity user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new HealingSnsAppException(ErrorCode.USERNAME_NOT_FOUND, String.format("%s은(는) 없는 회원입니다.", userName)));

        PostEntity post = PostEntity.builder()
                .title(postReq.getTitle())
                .body(postReq.getBody())
                .user(user)
                .build();
        postRepository.save(post);
        log.info("postId : {}", post.getId());

        return PostRes.builder()
                .postId(post.getId())
                .message("포스트 등록 완료")
                .build();
    }

    @Transactional(readOnly = true)
    public Page<PostViewRes> getPostList(Pageable pageable) {
        Page<PostViewRes> postList = postRepository.findAll(pageable).map(PostViewRes::of);
        return postList;
    }

    @Transactional
    public PostViewRes getPostById(Integer postId) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new HealingSnsAppException(ErrorCode.POST_NOT_FOUND, "해당 포스트가 없습니다."));

        return PostViewRes.builder()
                        .id(post.getId())
                        .title(post.getTitle())
                        .body(post.getBody())
                        .userName(post.getUser().getUsername())
                        .createdAt(post.getCreatedAt())
                        .lastModifiedAt(post.getUpdatedAt())
                        .build();
    }

    @Transactional
    public PostRes updatePostById(Integer postId, PostReq postEditReq, String userName, String userRole) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new HealingSnsAppException(ErrorCode.POST_NOT_FOUND, "해당 포스트가 없습니다."));

        UserEntity user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new HealingSnsAppException(ErrorCode.USERNAME_NOT_FOUND, String.format("%s은(는) 없는 회원입니다.", userName)));
//        System.out.println("확인 = " + authentication.getAuthorities().iterator().next().getAuthority().equals(UserRole.ADMIN.getAuthority()));

        if(!userRole.equals(UserRole.ADMIN.getAuthority()) && !post.getUser().getUsername().equals(user.getUsername())) {
            throw new HealingSnsAppException(ErrorCode.INVALID_PERMISSION, "사용자가 권한이 없습니다.");
        }

        post.updatePost(postEditReq.getTitle(), postEditReq.getBody());

        return PostRes.builder()
                .postId(postId)
                .message("포스트 수정 완료")
                .build();
    }

    @Transactional
    public PostRes deletePostById(Integer postId, String userName, String userRole) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new HealingSnsAppException(ErrorCode.POST_NOT_FOUND, "해당 포스트가 없습니다."));

        UserEntity user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new HealingSnsAppException(ErrorCode.USERNAME_NOT_FOUND, String.format("%s은(는) 없는 회원입니다.", userName)));

        if(!userRole.equals(UserRole.ADMIN.getAuthority()) && !post.getUser().getUsername().equals(user.getUsername())) {
            throw new HealingSnsAppException(ErrorCode.INVALID_PERMISSION, "사용자가 권한이 없습니다.");
        }

        postRepository.deleteById(postId);

        return PostRes.builder()
                .postId(post.getId())
                .message("포스트 삭제 완료")
                .build();
    }
}
