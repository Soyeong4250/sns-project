package com.likelion.healing.fixture;

import com.likelion.healing.domain.entity.Post;

import java.sql.Timestamp;
import java.time.Instant;

public class PostEntityFixture {

    public static Post get(String userName, String password) {
        Post postEntity = Post.builder()
                .id(1)
                .user(UserEntityFixture.get(userName, password))
                .title("title1")
                .body("body1")
                .build();
        postEntity.setCreatedAt(Timestamp.from(Instant.now()));
        postEntity.setUpdatedAt(Timestamp.from(Instant.now()));
        return postEntity;
    }
}
