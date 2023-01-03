package com.likelion.healing.domain.dto;

import com.likelion.healing.domain.entity.UserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "회원가입 요청 dto")
public class UserJoinReq {

    @NotBlank(message = "userName은 필수 값입니다.")
    @Length(min = 3, max = 20, message = "userName의 길이를 확인하세요")
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣A-Za-z0-9_-]{3,20}$", message = "userName은 한글, 영대소문자, 숫자, -로만 이루어질 수 있습니다")
    @Schema(description = "회원 이름")
    private String userName;

    @NotBlank(message = "password는 필수 값입니다.")
    @Length(min = 6, max = 50, message = "password의 길이를 확인하세요")
    @Schema(description = "회원 비밀번호")
    private String password;

    public UserEntity toEntity(String password) {
        return UserEntity.builder()
                .userName(this.userName)
                .password(password)
                .build();
    }
}
