package com.likelion.healing.service;

import com.likelion.healing.domain.dto.PostAddReq;
import com.likelion.healing.domain.dto.PostRes;
import com.likelion.healing.domain.dto.PostViewRes;
import com.likelion.healing.domain.entity.Post;
import com.likelion.healing.domain.entity.User;
import com.likelion.healing.exception.ErrorCode;
import com.likelion.healing.exception.HealingSnsAppException;
import com.likelion.healing.repository.PostRepository;
import com.likelion.healing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostRes addPost(PostAddReq postAddReq, String userName) {
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new HealingSnsAppException(ErrorCode.USERNAME_NOT_FOUND, String.format("%s은(는) 없는 회원입니다.", userName)));

        Post post = Post.builder()
                .title(postAddReq.getTitle())
                .body(postAddReq.getBody())
                .user(user)
                .build();
        Post savedPost = postRepository.save(post);
        return PostRes.builder()
                .postId(savedPost.getId())
                .message("포스트 등록 완료")
                .build();
    }

    public Page<PostViewRes> getPostList(Pageable pageable) {
        Page<PostViewRes> postList = postRepository.findAllByOrderByCreatedAtDesc(pageable).map(PostViewRes::of);
        return postList;
    }

    public PostViewRes getPostById(Integer postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new HealingSnsAppException(ErrorCode.POST_NOT_FOUND, "해당 포스트가 없습니다."));

        return PostViewRes.builder()
                        .id(post.getId())
                        .title(post.getTitle())
                        .body(post.getBody())
                        .userName(post.getUser().getUserName())
                        .createdAt(post.getCreatedAt())
                        .lastModifiedAt(post.getUpdatedAt())
                        .build();
    }
}
