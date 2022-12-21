package com.likelion.healing.domain.dto;

import com.likelion.healing.domain.entity.User;
import com.likelion.healing.domain.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserJoinReq {

    private String userName;
    private String password;

    public User toEntity() {
        return User.builder()
                .userName(this.userName)
                .password(this.password)
                .role(UserRole.USER)
                .build();
    }
}
