package com.likelion.healing.fixture;

import com.likelion.healing.domain.entity.LikeEntity;

import java.time.LocalDateTime;

public class LikeEntityFixture {

    public static LikeEntity get(String userName, String password) {
        LikeEntity likeEntity = LikeEntity.builder()
                                        .post(PostEntityFixture.get(userName, password))
                                        .user(UserEntityFixture.get(userName, password))
                                        .build();
        likeEntity.setCreatedAt(LocalDateTime.now());
        likeEntity.setUpdatedAt(LocalDateTime.now());
        return likeEntity;
    }
}
