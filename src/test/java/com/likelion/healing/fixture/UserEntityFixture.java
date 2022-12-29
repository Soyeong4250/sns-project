package com.likelion.healing.fixture;

import com.likelion.healing.domain.entity.UserEntity;
import com.likelion.healing.domain.entity.UserRole;

import java.time.LocalDateTime;

public class UserEntityFixture {

    public static UserEntity get(String userName, String password) {
        UserEntity user = new UserEntity();
        user.setId(1);
        user.setUserName(userName);
        user.setPassword(password);
        user.setRole(UserRole.USER);
        user.setCreatedAt(LocalDateTime.now());
        return user;
    }
}
