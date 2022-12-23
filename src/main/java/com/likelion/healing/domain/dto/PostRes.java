package com.likelion.healing.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Schema(description = "포스트 등록, 수정, 삭제 응답 dto")
public class PostRes {

    @Schema(description = "로직 수행 결과")
    private String message;

    @Schema(description = "포스트 번호")
    private Integer postId;

}
