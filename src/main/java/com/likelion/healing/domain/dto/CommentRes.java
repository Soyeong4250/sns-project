package com.likelion.healing.domain.dto;

import com.likelion.healing.domain.entity.CommentEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class CommentRes {

    @Schema(description = "댓글 번호")
    private Integer id;

    @Schema(description = "댓글 내용")
    private String comment;

    @Schema(description = "작성자")
    private String userName;

    @Schema(description = "포스트 번호")
    private Integer postId;

    @Schema(description = "생성 시간")
    private LocalDateTime createdAt;

    @Schema(description = "마지막 수정 시간")
    private LocalDateTime lastModifiedAt;

    public static CommentRes of(CommentEntity commentEntity) {
        return CommentRes.builder()
                .id(commentEntity.getId())
                .comment(commentEntity.getComment())
                .userName(commentEntity.getUser().getUsername())
                .postId(commentEntity.getPost().getId())
                .createdAt(commentEntity.getCreatedAt())
                .lastModifiedAt(commentEntity.getUpdatedAt())
                .build();
    }
}
