package com.likelion.healing.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CommentDeleteRes {

    @Schema(description = "로직 수행 결과")
    private String message;
    @Schema(description = "댓글 번호")
    private Integer id;

}
