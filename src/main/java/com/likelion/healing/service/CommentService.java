package com.likelion.healing.service;

import com.likelion.healing.domain.dto.CommentDeleteRes;
import com.likelion.healing.domain.dto.CommentReq;
import com.likelion.healing.domain.dto.CommentRes;
import com.likelion.healing.domain.entity.*;
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
    private final AlarmService alarmService;

    @Transactional
    public CommentRes createComment(Integer postId, @Valid CommentReq commentReq, String userName) {

        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new HealingSnsAppException(ErrorCode.POST_NOT_FOUND, String.format("%d번 포스트가 존재하지 않습니다.", postId)));

        UserEntity user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new HealingSnsAppException(ErrorCode.USERNAME_NOT_FOUND, String.format("%s은(는) 존재하지 않는 회원입니다", userName)));
        CommentEntity commentEntity = commentReq.toEntity();
        commentEntity.setUser(user);
        commentEntity.setPost(post);
        commentRepository.save(commentEntity);

        log.info("userName : {}", user.getUserName());
        log.info("postId : {}", post.getId());
        alarmService.sendAlarm(user, post, AlarmType.NEW_COMMENT_ON_POST);

        return CommentRes.of(commentEntity);
    }

    @Transactional(readOnly = true)
    public Page<CommentRes> getCommentList(Integer postId, Pageable pageable) {
       postRepository.findById(postId)
                .orElseThrow(() -> new HealingSnsAppException(ErrorCode.POST_NOT_FOUND, String.format("%d번 포스트가 존재하지 않습니다.", postId)));

        Page<CommentRes> commentList = commentRepository.findByPostId(postId, pageable).map(CommentRes::of);
        return commentList;
    }

    @Transactional
    public CommentRes updateComment(Integer postId, Integer commentId, @Valid CommentReq commentReq, String userName) {
        postRepository.findById(postId)
                .orElseThrow(() -> new HealingSnsAppException(ErrorCode.POST_NOT_FOUND, String.format("%d번 포스트가 존재하지 않습니다.", postId)));

        CommentEntity comment = commentRepository.findByPostIdAndId(postId, commentId)
                .orElseThrow(() -> new HealingSnsAppException(ErrorCode.COMMENT_NOT_FOUND, String.format("%d번 포스트에는 %d번 댓글이 존재하지 않습니다.", postId, commentId)));

        UserEntity user = userRepository.findByUserName(userName)
                                        .orElseThrow(() -> new HealingSnsAppException(ErrorCode.USERNAME_NOT_FOUND, String.format("%s은(는) 존재하지 않는 회원입니다.", userName)));

        if(!user.getRole().equals(UserRole.ADMIN.getAuthority()) && !user.getUserName().equals(comment.getUser().getUserName())) {
            throw new HealingSnsAppException(ErrorCode.INVALID_PERMISSION, "사용자가 권한이 없습니다.");
        }

        comment.updateComment(commentReq.getComment(), user);
        log.info("comment.getComment : {}", comment.getComment());
        log.info("comment.oldLastUpdate : {}", comment.getUpdatedAt());
        commentRepository.flush();
        log.info("comment.newLastUpdate : {}", comment.getUpdatedAt());

        return CommentRes.of(comment);
    }

    @Transactional
    public CommentDeleteRes deleteComment(Integer postId, Integer commentId, String userName) {
        postRepository.findById(postId)
                .orElseThrow(() -> new HealingSnsAppException(ErrorCode.POST_NOT_FOUND, String.format("%d번 포스트가 존재하지 않습니다.", postId)));

        UserEntity user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new HealingSnsAppException(ErrorCode.USERNAME_NOT_FOUND, String.format("%s은(는) 존재하지 않는 회원입니다.", userName)));


        CommentEntity comment = commentRepository.findByPostIdAndId(postId, commentId)
                .orElseThrow(() -> new HealingSnsAppException(ErrorCode.COMMENT_NOT_FOUND, String.format("%d번 포스트에는 %d번 댓글이 존재하지 않습니다.", postId, commentId)));

        if(!user.getRole().equals(UserRole.ADMIN.getAuthority()) && !user.getUserName().equals(comment.getUser().getUserName())) {
            throw new HealingSnsAppException(ErrorCode.INVALID_PERMISSION, "사용자가 권한이 없습니다.");
        }
        commentRepository.deleteById(commentId);
        return new CommentDeleteRes("댓글 삭제 완료", commentId);
    }
}
