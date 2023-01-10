package com.likelion.healing.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserRole {

    MASTER(Authority.MASTER),
    ADMIN(Authority.ADMIN),
    USER(Authority.USER)
    ;

    private final String authority;

    public static class Authority {
        public static final String MASTER = "ROLE_MASTER";
        public static final String ADMIN = "ROLE_ADMIN";
        public static final String USER = "ROLE_USER";
    }
}
