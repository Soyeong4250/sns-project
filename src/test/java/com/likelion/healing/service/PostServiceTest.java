package com.likelion.healing.service;

import com.likelion.healing.domain.dto.PostAddReq;
import com.likelion.healing.domain.dto.PostAddRes;
import com.likelion.healing.domain.entity.Post;
import com.likelion.healing.domain.entity.User;
import com.likelion.healing.domain.entity.UserRole;
import com.likelion.healing.exception.ErrorCode;
import com.likelion.healing.exception.HealingSnsAppException;
import com.likelion.healing.repository.PostRepository;
import com.likelion.healing.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class PostServiceTest {

    private PostRepository postRepository = Mockito.mock(PostRepository.class);
    private UserRepository userRepository = Mockito.mock(UserRepository.class);
    private PostService postService = new PostService(postRepository, userRepository);

    @Test
    @DisplayName("포스트 등록 성공")
    void successfulAddPost() {
        User givenUser = User.builder()
                .userName("Soyeong")
                .role(UserRole.USER)
                .build();
        Mockito.when(userRepository.findByUserName("Soyeong"))
                .thenReturn(Optional.of(givenUser));

        Post givenPost = Post.builder()
                .id(1)
                .title("title1")
                .body("body1")
                .user(givenUser)
                .build();
        Mockito.when(postRepository.save(any(Post.class)))
                .thenReturn(givenPost);


        PostAddRes postAddRes = postService.addPost(
                new PostAddReq("title1", "body1"), givenUser.getUserName());

        Assertions.assertEquals(1, postAddRes.getPostId());
        Assertions.assertEquals("포스트 등록 완료", postAddRes.getMessage());

        verify(postRepository).save(any());
    }

    @Test
    @DisplayName("포스트 등록 실패 - 회원이 존재하지 않을 때")
    void notFoundUser() {
        User givenUser = User.builder()
                .userName("Soyeong")
                .role(UserRole.USER)
                .build();
        Mockito.when(userRepository.findByUserName("Soyeong"))
                .thenThrow(new HealingSnsAppException(ErrorCode.NOT_FOUND, String.format("%s은(는) 없는 회원입니다.", givenUser.getUserName())));

        Post givenPost = Post.builder()
                .id(1)
                .title("title1")
                .body("body1")
                .user(givenUser)
                .build();
        Mockito.when(postRepository.save(any(Post.class)))
                .thenReturn(givenPost);

        try {
            PostAddRes postAddRes = postService.addPost(
                    new PostAddReq("title1", "body1"), givenUser.getUserName());
        } catch (HealingSnsAppException e) {
            Assertions.assertEquals(ErrorCode.NOT_FOUND, e.getErrorCode());
            Assertions.assertEquals("Soyeong은(는) 없는 회원입니다.", e.getMessage());
        }

        verify(postRepository, never()).save(any());
    }
}