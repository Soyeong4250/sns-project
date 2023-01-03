package com.likelion.healing.domain.dto;

import com.likelion.healing.domain.entity.CommentEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class CommentAddRes {

    private Integer id;
    private String comment;
    private String userName;
    private Integer postId;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;

    public static CommentAddRes of(CommentEntity commentEntity) {
        return CommentAddRes.builder()
                .id(commentEntity.getId())
                .comment(commentEntity.getComment())
                .userName(commentEntity.getUser().getUsername())
                .postId(commentEntity.getPost().getId())
                .createdAt(commentEntity.getCreatedAt())
                .lastModifiedAt(commentEntity.getUpdatedAt())
                .build();
    }
}
