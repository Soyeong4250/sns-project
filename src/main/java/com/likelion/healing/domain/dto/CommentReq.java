package com.likelion.healing.domain.dto;

import com.likelion.healing.domain.entity.CommentEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class CommentReq {

    @Schema(description = "댓글 내용")
    private String comment;

    public CommentEntity toEntity() {
        return CommentEntity.builder()
                .comment(this.comment)
                .build();
    }
}
