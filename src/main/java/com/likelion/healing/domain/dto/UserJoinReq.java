package com.likelion.healing.domain.dto;

import com.likelion.healing.domain.entity.UserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "회원가입 요청 dto")
public class UserJoinReq {

    @Schema(description = "회원 이름")
    private String userName;

    @Schema(description = "회원 비밀번호")
    private String password;

    public UserEntity toEntity(String password) {
        return UserEntity.builder()
                .userName(this.userName)
                .password(password)
                .build();
    }
}
