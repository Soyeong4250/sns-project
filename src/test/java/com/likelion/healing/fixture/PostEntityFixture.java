package com.likelion.healing.fixture;

import com.likelion.healing.domain.entity.Post;

public class PostEntityFixture {

    public static Post get(String userName, String password) {
        Post postEntity = Post.builder()
                .id(1)
                .user(UserEntityFixture.get(userName, password))
                .title("title1")
                .body("body1")
                .build();
        return postEntity;
    }
}
