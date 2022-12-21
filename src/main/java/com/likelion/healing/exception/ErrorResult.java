package com.likelion.healing.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResult {

    private ErrorCode errorCode;
    private String message;

}
