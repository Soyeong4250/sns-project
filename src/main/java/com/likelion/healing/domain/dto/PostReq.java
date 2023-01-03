package com.likelion.healing.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Schema(description = "포스트 등록, 수정, 삭제 요청 dto")
public class PostReq {

    @NotBlank(message = "title은 필수 값입니다.")
    @Schema(description = "포스트 제목")
    private String title;

    @NotBlank(message = "body는 필수 값입니다.")
    @Length(max = 300)
    @Schema(description = "포스트 내용")
    private String body;

}
