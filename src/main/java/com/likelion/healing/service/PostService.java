package com.likelion.healing.service;

import com.likelion.healing.domain.dto.PostReq;
import com.likelion.healing.domain.dto.PostRes;
import com.likelion.healing.domain.dto.PostViewRes;
import com.likelion.healing.domain.entity.LikeEntity;
import com.likelion.healing.domain.entity.PostEntity;
import com.likelion.healing.domain.entity.UserEntity;
import com.likelion.healing.domain.entity.UserRole;
import com.likelion.healing.exception.ErrorCode;
import com.likelion.healing.exception.HealingSnsAppException;
import com.likelion.healing.repository.LikeRepository;
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

    private final LikeRepository likeRepository;
    private AlarmService alarmService;

    @Transactional
    public PostRes createPost(PostReq postReq, String userName) {
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
    public PostRes updatePostById(Integer postId, PostReq postEditReq, String userName) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new HealingSnsAppException(ErrorCode.POST_NOT_FOUND, "해당 포스트가 없습니다."));

        UserEntity user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new HealingSnsAppException(ErrorCode.USERNAME_NOT_FOUND, String.format("%s은(는) 없는 회원입니다.", userName)));
//        System.out.println("확인 = " + authentication.getAuthorities().iterator().next().getAuthority().equals(UserRole.ADMIN.getAuthority()));

        if(!user.getRole().equals(UserRole.ADMIN) && !post.getUser().getUsername().equals(user.getUsername())) {
            throw new HealingSnsAppException(ErrorCode.INVALID_PERMISSION, "사용자가 권한이 없습니다.");
        }

        post.updatePost(postEditReq.getTitle(), postEditReq.getBody());

        return PostRes.builder()
                .postId(postId)
                .message("포스트 수정 완료")
                .build();
    }

    @Transactional
    public PostRes deletePostById(Integer postId, String userName) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new HealingSnsAppException(ErrorCode.POST_NOT_FOUND, "해당 포스트가 없습니다."));

        UserEntity user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new HealingSnsAppException(ErrorCode.USERNAME_NOT_FOUND, String.format("%s은(는) 없는 회원입니다.", userName)));

        if(!user.getRole().equals(UserRole.ADMIN) && !post.getUser().getUsername().equals(user.getUsername())) {
            throw new HealingSnsAppException(ErrorCode.INVALID_PERMISSION, "사용자가 권한이 없습니다.");
        }

        postRepository.deleteById(postId);

        return PostRes.builder()
                .postId(post.getId())
                .message("포스트 삭제 완료")
                .build();
    }

    @Transactional(readOnly = true)
    public Page<PostViewRes> getMyFeed(Pageable pageable, String userName) {
        UserEntity user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new HealingSnsAppException(ErrorCode.USERNAME_NOT_FOUND, String.format("%s은(는) 없는 회원입니다.", userName)));

        return postRepository.findByUser(user, pageable).map(PostViewRes::of);
    }

    @Transactional
    public void increaseLike(Integer postId, String userName) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new HealingSnsAppException(ErrorCode.POST_NOT_FOUND, "해당 포스트가 없습니다."));

        UserEntity user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new HealingSnsAppException(ErrorCode.USERNAME_NOT_FOUND, String.format("%s은(는) 없는 회원입니다.", userName)));

        likeRepository.findByPostAndUser(post, user)
                .ifPresent(likeEntity -> {
                    throw new HealingSnsAppException(ErrorCode.DUPLICATED_LIKE, "이미 좋아요를 눌렀습니다.");
                });

        likeRepository.save(LikeEntity.builder().post(post).user(user).build());

        alarmService.sendAlarm(user, post);
    }

    @Transactional
    public void decreaseLike(Integer postId, String userName) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new HealingSnsAppException(ErrorCode.POST_NOT_FOUND, "해당 포스트가 없습니다."));

        UserEntity user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new HealingSnsAppException(ErrorCode.USERNAME_NOT_FOUND, String.format("%s은(는) 없는 회원입니다.", userName)));

        LikeEntity like = likeRepository.findByPostAndUser(post, user)
                .orElseThrow(() -> new HealingSnsAppException(ErrorCode.LIKE_NOT_FOUND, "좋아요를 누른 적이 없습니다."));

        likeRepository.delete(like);
    }

    public Integer countLike(Integer postId) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new HealingSnsAppException(ErrorCode.POST_NOT_FOUND, "해당 포스트가 없습니다."));

        Integer likesCnt = likeRepository.findByPost(post);
        return likesCnt;
    }
}
