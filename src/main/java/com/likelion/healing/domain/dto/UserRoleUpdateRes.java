package com.likelion.healing.domain.dto;

import com.likelion.healing.domain.entity.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Schema(description = "권한 변경 응답 dto")
public class UserRoleUpdateRes {

    @Schema(description = "로직 수행 결과")
    private String message;
    @Schema(description = "권한 변경 결과")
    private UserRole role;

}
