package com.likelion.healing.service;

import com.likelion.healing.domain.dto.CommentReq;
import com.likelion.healing.domain.dto.CommentRes;
import com.likelion.healing.domain.entity.CommentEntity;
import com.likelion.healing.domain.entity.PostEntity;
import com.likelion.healing.domain.entity.UserEntity;
import com.likelion.healing.exception.ErrorCode;
import com.likelion.healing.exception.HealingSnsAppException;
import com.likelion.healing.repository.CommentRepository;
import com.likelion.healing.repository.PostRepository;
import com.likelion.healing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Transactional
    public CommentRes createComment(Integer postId, CommentReq commentReq, String userName) {

        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new HealingSnsAppException(ErrorCode.POST_NOT_FOUND, String.format("%d번 포스트가 존재하지 않습니다.", postId)));

        UserEntity user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new HealingSnsAppException(ErrorCode.USERNAME_NOT_FOUND, String.format("%s은(는) 존재하지 않는 회원입니다", userName)));
        CommentEntity commentEntity = commentReq.toEntity();
        commentEntity.setUser(user);
        commentEntity.setPost(post);
        commentRepository.save(commentEntity);
        log.info("commentEntity id: {}", commentEntity.getId());
        return CommentRes.of(commentEntity);
    }
}
