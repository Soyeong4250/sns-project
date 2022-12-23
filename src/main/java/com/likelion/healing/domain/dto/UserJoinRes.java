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
@Schema(description = "회원가입 응답 dto")
public class UserJoinRes {

    @Schema(description = "회원 번호")
    private Integer userId;

    @Schema(description = "회원 이름")
    private String userName;

}
