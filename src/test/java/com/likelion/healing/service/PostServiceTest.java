package com.likelion.healing.service;

import com.likelion.healing.domain.dto.PostAddReq;
import com.likelion.healing.domain.dto.PostRes;
import com.likelion.healing.domain.dto.PostViewRes;
import com.likelion.healing.domain.entity.Post;
import com.likelion.healing.domain.entity.User;
import com.likelion.healing.exception.ErrorCode;
import com.likelion.healing.exception.HealingSnsAppException;
import com.likelion.healing.fixture.PostEntityFixture;
import com.likelion.healing.fixture.TestInfoFixture;
import com.likelion.healing.repository.PostRepository;
import com.likelion.healing.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PostServiceTest {

    private PostRepository postRepository = mock(PostRepository.class);
    private UserRepository userRepository = mock(UserRepository.class);
    private PostService postService = new PostService(postRepository, userRepository);

    @Test
    @DisplayName("포스트 등록 성공")
    void successfulAddPost() {
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();

        User mockUser = mock(User.class);
        Post mockPost = mock(Post.class);

        Mockito.when(userRepository.findByUserName(fixture.getUserName()))
                .thenReturn(Optional.of(mockUser));

        Mockito.when(postRepository.save(any(Post.class)))
                .thenReturn(mockPost);

        Assertions.assertDoesNotThrow(() -> postService.addPost(new PostAddReq(fixture.getTitle(), fixture.getBody()), fixture.getUserName()));
    }

    @Test
    @DisplayName("포스트 등록 실패 - 회원이 존재하지 않을 때")
    void notFoundUser() {
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();

        User givenUser = mock(User.class);
        Mockito.when(userRepository.findByUserName(fixture.getUserName()))
                .thenThrow(new HealingSnsAppException(ErrorCode.USERNAME_NOT_FOUND, String.format("%s은(는) 없는 회원입니다.", fixture.getUserName())));

        Post mockPost = mock(Post.class);
        Mockito.when(postRepository.save(any(Post.class)))
                .thenReturn(mockPost);

        try {
            PostRes postRes = postService.addPost(
                    new PostAddReq("title1", "body1"), givenUser.getUserName());
        } catch (HealingSnsAppException e) {
            Assertions.assertEquals(ErrorCode.USERNAME_NOT_FOUND, e.getErrorCode());
        }

        verify(postRepository, never()).save(any());
    }

    @Test
    @DisplayName("포스트 단건 조회 성공")
    void successfulGetPostById() {
        TestInfoFixture.TestInfo fixture = TestInfoFixture.get();
        User givenUser = User.builder()
                            .userName(fixture.getUserName())
                            .build();
        Mockito.when(userRepository.findByUserName(fixture.getUserName()))
                .thenReturn(Optional.of(givenUser));

        Post givenPost = PostEntityFixture.get(fixture.getUserName(), fixture.getPassword());

        Mockito.when(postRepository.findById(any(Integer.class)))
                .thenReturn(Optional.of(givenPost));

        PostViewRes postViewRes = postService.getPostById(fixture.getPostId());

        Assertions.assertEquals(fixture.getPostId(), postViewRes.getId());
        Assertions.assertEquals(fixture.getTitle(), postViewRes.getTitle());
        Assertions.assertEquals(fixture.getBody(), postViewRes.getBody());
        Assertions.assertEquals(fixture.getUserName(), postViewRes.getUserName());
        Assertions.assertNotNull(postViewRes.getCreatedAt());
        Assertions.assertNotNull(postViewRes.getLastModifiedAt());

        verify(postRepository).findById(any());
    }
}