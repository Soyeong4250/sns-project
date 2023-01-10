package com.likelion.healing.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.likelion.healing.domain.entity.PostEntity;
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
@Schema(description = "포스트 조회 응답 dto")
public class PostViewRes {

    @Schema(description = "포스트 번호")
    private Integer id;

    @Schema(description = "포스트 제목")
    private String title;

    @Schema(description = "포스트 내용")
    private String body;

    @Schema(description = "작성자")
    private String userName;

    @Schema(description = "포스트 작성일자")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;

    @Schema(description = "포스트 마지막 수정일자")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime lastModifiedAt;

    public static PostViewRes of(PostEntity post) {
        return PostViewRes.builder()
                .id(post.getId())
                .title(post.getTitle())
                .body(post.getBody())
                .userName(post.getUser().getUserName())
                .createdAt(post.getCreatedAt())
                .lastModifiedAt(post.getUpdatedAt())
                .build();
    }
}
