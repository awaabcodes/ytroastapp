package com.ytroast.backend.exception;

import com.ytroast.backend.auth.exception.AuthDeniedException;
import com.ytroast.backend.auth.exception.InvalidCallBackException;
import com.ytroast.backend.auth.exception.OAuthException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(InvalidCallBackException.class)
    public ResponseEntity<Map<String,String>> invalidCallBack(InvalidCallBackException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));

    }
    @ExceptionHandler(AuthDeniedException.class)
    public ResponseEntity<Map<String,String>> authDenied(AuthDeniedException e){
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));

    }
    @ExceptionHandler(OAuthException.class)
    public ResponseEntity<Map<String,String>> oAuthFailure(OAuthException e){
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of("error", e.getMessage()));

    }
}
