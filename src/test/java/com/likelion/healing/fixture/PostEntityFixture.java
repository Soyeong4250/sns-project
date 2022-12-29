package com.likelion.healing.fixture;

import com.likelion.healing.domain.entity.PostEntity;

import java.time.LocalDateTime;

public class PostEntityFixture {

    public static PostEntity get(String userName, String password) {
        PostEntity post = PostEntity.builder()
                .id(1)
                .user(UserEntityFixture.get(userName, password))
                .title("title1")
                .body("body1")
                .build();
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        return post;
    }
}
