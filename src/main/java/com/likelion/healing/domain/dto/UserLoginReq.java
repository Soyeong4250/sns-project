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
@Schema(description = "로그인 요청 dto")
public class UserLoginReq {

    @Schema(description = "회원 이름")
    private String userName;

    @Schema(description = "회원 비밀번호")
    private String password;

}
