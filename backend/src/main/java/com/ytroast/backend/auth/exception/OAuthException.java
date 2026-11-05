package com.ytroast.backend.auth.exception;

public class OAuthException extends RuntimeException {
    public OAuthException(String message, Throwable cause) {
        super(message, cause);
    }
}
