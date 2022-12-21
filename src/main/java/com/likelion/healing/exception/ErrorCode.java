package com.likelion.healing.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    DUPLICATED_USER_NAME(HttpStatus.CONFLICT, "중복된 userName입니다.");

    private HttpStatus status;
    private String message;

}