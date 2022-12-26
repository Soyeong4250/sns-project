package com.likelion.healing.domain.dto;

import com.likelion.healing.domain.entity.User;
import com.likelion.healing.domain.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class AdminViewRes {

    private Integer id;
    private String userName;
    private UserRole role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public static AdminViewRes of(User user) {
        return AdminViewRes.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .deletedAt(user.getDeletedAt())
                .build();
    }
}
