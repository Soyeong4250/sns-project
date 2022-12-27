package com.likelion.healing.fixture;

import com.likelion.healing.domain.entity.UserRole;
import lombok.Getter;
import lombok.Setter;

public class TestInfoFixture {
    public static TestInfo get() {
        TestInfo info = new TestInfo();
        info.setPostId(1);
        info.setUserId(1);
        info.setUserName("Soyeong");
        info.setPassword("12345");
        info.setRole(UserRole.USER);
        info.setTitle("title1");
        info.setBody("body1");
        return info;
    }
    
    @Getter
    @Setter
    public static class TestInfo {
        private Integer postId;
        private Integer userId;
        private String userName;
        private String password;
        private UserRole role;
        private String title;
        private String body;
    }
}
