package com.likelion.healing.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Schema(description = "로그인 요청 dto")
public class UserLoginReq {

    @NotBlank(message = "userName은 필수 값입니다.")
    @Schema(description = "회원 이름")
    private String userName;

    @NotBlank(message = "password는 필수 값입니다.")
    @Schema(description = "회원 비밀번호")
    private String password;

}
