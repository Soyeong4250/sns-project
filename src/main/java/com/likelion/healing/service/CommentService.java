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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;

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

    @Transactional(readOnly = true)
    public Page<CommentRes> getCommentList(Integer postId, Pageable pageable) {
       postRepository.findById(postId)
                .orElseThrow(() -> new HealingSnsAppException(ErrorCode.POST_NOT_FOUND, String.format("%d번 포스트가 존재하지 않습니다.", postId)));

        Page<CommentRes> commentList = commentRepository.findByPostId(postId, pageable).map(CommentRes::of);
        return commentList;
    }

    public CommentRes updateComment(Integer postId, Integer commentId, @Valid CommentReq commentReq, String userName) {
        postRepository.findById(postId)
                .orElseThrow(() -> new HealingSnsAppException(ErrorCode.POST_NOT_FOUND, String.format("%d번 포스트가 존재하지 않습니다.", postId)));

        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new HealingSnsAppException(ErrorCode.COMMENT_NOT_FOUND, String.format("%d번 댓글이 존재하지 않습니다.", commentId)));

        UserEntity user = userRepository.findByUserName(userName)
                                        .orElseThrow(() -> new HealingSnsAppException(ErrorCode.USERNAME_NOT_FOUND, String.format("%s은(는) 존재하지 않는 회원입니다.", userName)));

        if(!user.getUsername().equals(comment.getUser().getUsername())) {
            throw new HealingSnsAppException(ErrorCode.INVALID_PERMISSION, "사용자가 권한이 없습니다.");
        }

        comment.updateComment(commentReq.getComment());

        return CommentRes.of(comment);
    }
}
