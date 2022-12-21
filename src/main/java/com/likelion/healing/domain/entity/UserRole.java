package com.likelion.healing.domain.entity;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum UserRole {

    ADMIN("admin"),
    USER("user")
    ;

    private String name;
}
