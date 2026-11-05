package com.ytroast.backend.auth.exception;

public class AuthDeniedException extends RuntimeException {
    public AuthDeniedException(String message) {
        super(message);
    }
}
