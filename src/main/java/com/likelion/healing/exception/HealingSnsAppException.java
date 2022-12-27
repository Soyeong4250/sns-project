package com.likelion.healing.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HealingSnsAppException extends RuntimeException{

    private ErrorCode errorCode;
    private String message;

}
