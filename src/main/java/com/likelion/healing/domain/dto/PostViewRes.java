package com.likelion.healing.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class PostViewRes {

    private Integer id;
    private String title;
    private String body;
    private String userName;
    private Timestamp createdAt;

}
