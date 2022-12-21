package com.likelion.healing.exception;


import com.likelion.healing.domain.entity.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionManager {

    @ExceptionHandler(HealingSnsAppException.class)
    public ResponseEntity<?> healingSnsAppExceptionHandler(HealingSnsAppException e) {
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(Response.error(e));
    }

}
