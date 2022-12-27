package com.likelion.healing.domain.dto;

import com.likelion.healing.domain.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class UpdateUserRoleRes {

    private String message;
    private UserRole role;

}
