package com.likelion.healing.exception;


import com.likelion.healing.controller.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

@RestControllerAdvice
public class ExceptionManager {

    @ExceptionHandler(HealingSnsAppException.class)
    public ResponseEntity<?> healingSnsAppExceptionHandler(HealingSnsAppException e) {
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(Response.error(new ErrorResult(e.getErrorCode(), e.getMessage())));
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<?> databaseExceptionHandler(SQLException e) {
        ErrorResult errorResult = new ErrorResult(ErrorCode.DATABASE_ERROR, e.getMessage());
        return ResponseEntity.status(errorResult.getErrorCode().getStatus())
                .body(Response.error(errorResult));
    }

}
