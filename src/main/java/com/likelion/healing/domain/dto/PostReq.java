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
@Schema(description = "포스트 등록, 수정, 삭제 요청 dto")
public class PostReq {

    @Schema(description = "포스트 제목")
    private String title;

    @Schema(description = "포스트 내용")
    private String body;

}
