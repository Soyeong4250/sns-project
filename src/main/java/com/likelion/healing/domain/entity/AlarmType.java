package com.likelion.healing.domain.entity;

import lombok.Getter;

@Getter
public enum AlarmType {

    NEW_COMMENT_ON_POST("new cmment!"),
    NEW_LIKE_ON_POST("new like!")
    ;

    private final String label;

    AlarmType(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}
