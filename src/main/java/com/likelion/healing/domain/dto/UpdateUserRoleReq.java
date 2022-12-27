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
@Schema(description = "권한 변경 요청 dto")
public class UpdateUserRoleReq {

    @Schema(description = "변경하고 싶은 권한")
    private UserRole role;

}
