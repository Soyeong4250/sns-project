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

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    private final LikeRepository likeRepository;
    private final AlarmService alarmService;

    @Transactional
    public PostRes createPost(PostReq postReq, String userName) {
        UserEntity user = findUserByUserName(userName);

        PostEntity post = PostEntity.createPost(postReq.getTitle(), postReq.getBody(), user);

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
        PostEntity post = findPostById(postId);
        log.info("post.getComments.size : {}", post.getComments().size());
        return PostViewRes.builder()
                        .id(post.getId())
                        .title(post.getTitle())
                        .body(post.getBody())
                        .userName(post.getUser().getUserName())
                        .createdAt(post.getCreatedAt())
                        .lastModifiedAt(post.getUpdatedAt())
                        .build();
    }

    @Transactional
    public PostRes updatePostById(Integer postId, PostReq postEditReq, String userName) {
        PostEntity post = findPostById(postId);

        UserEntity user = findUserByUserName(userName);

        if(!user.getRole().equals(UserRole.ADMIN) && !post.getUser().getUserName().equals(user.getUserName())) {
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
        PostEntity post = findPostById(postId);

        UserEntity user = findUserByUserName(userName);

        if(!user.getRole().equals(UserRole.ADMIN) && !post.getUser().getUserName().equals(user.getUserName())) {
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
        UserEntity user = findUserByUserName(userName);

        return postRepository.findByUser(user, pageable).map(PostViewRes::of);
    }

    @Transactional
    public String pushLike(Integer postId, String userName) {
        PostEntity post = findPostById(postId);

        UserEntity user = findUserByUserName(userName);

        // void를 return
        /*likeRepository.findByPostAndUser(post, user)
                        .ifPresentOrElse((likeEntity) -> {
                            likeRepository.delete(likeEntity);
                        },
                        () -> {
                            likeRepository.save(LikeEntity.builder().user(user).post(post).build());
                        });*/

        Optional<LikeEntity> like = likeRepository.findByPostAndUser(post, user);
        if(like.isEmpty()) {
            log.debug("like.isEmpty() 실행");
            likeRepository.save(LikeEntity.builder().post(post).user(user).build());
            return "좋아요를 눌렀습니다.";
        } else {
            log.info("like Id : {}", like.get().getId());
            likeRepository.delete(like.get());
            return "좋아요를 취소했습니다.";
        }
    }

    @Transactional
    public Integer countLike(Integer postId) {
        PostEntity post = findPostById(postId);

        Integer likesCnt = likeRepository.findByPost(post);
        return likesCnt;
    }

    // 중복 메서드
    private PostEntity findPostById(Integer postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new HealingSnsAppException(ErrorCode.POST_NOT_FOUND, "해당 포스트가 없습니다."));
    }

    private UserEntity findUserByUserName(String userName) {
        return userRepository.findByUserName(userName)
                .orElseThrow(() -> new HealingSnsAppException(ErrorCode.USERNAME_NOT_FOUND, String.format("%s은(는) 없는 회원입니다.", userName)));
    }

}
