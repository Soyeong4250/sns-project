package com.likelion.healing.fixture;

import com.likelion.healing.domain.entity.User;
import com.likelion.healing.domain.entity.UserRole;

import java.time.Instant;
import java.time.LocalDateTime;

public class UserEntityFixture {

    public static User get(String userName, String password) {
        User userEntity = new User();
        userEntity.setId(1);
        userEntity.setUserName(userName);
        userEntity.setPassword(password);
        userEntity.setRole(UserRole.USER);
        userEntity.setCreatedAt(LocalDateTime.from(Instant.now()));
        return userEntity;
    }
}
