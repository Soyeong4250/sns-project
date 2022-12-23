package com.likelion.healing.fixture;

import lombok.Getter;
import lombok.Setter;

public class TestInfoFixture {
    public static TestInfo get() {
        TestInfo info = new TestInfo();
        info.setPostId(1);
        info.setUserId(1);
        info.setUserName("Soyeong");
        info.setPassword("12345");
        info.setTitle("title1");
        info.setPassword("body1");
        return info;
    }
    
    @Getter
    @Setter
    public static class TestInfo {
        private Integer postId;
        private Integer userId;
        private String userName;
        private String password;
        private String title;
        private String body;
    }
}
