package com.likelion.healing.domain.entity;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum UserRole {

    ADMIN("ADMIN"),
    USER("USER")
    ;

    private String name;
}
