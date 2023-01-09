package com.likelion.healing.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Response<T> {
    private String resultCode;
    private T result;

    private static final String SUCCESS = "SUCCESS";
    private static final String ERROR = "ERROR";

    public static <T> Response<T> error(T result) {
        return new Response<>(ERROR, result);
    }

    public static <T> Response<T> success(T result) {
        return new Response<>(SUCCESS, result);
    }

}
