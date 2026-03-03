package com.luis.spring.security.msauth_poc.exception;

/**
 * @author Luis Balarezo
 **/
public class TokenException extends RuntimeException {

    public TokenException(String message) {
        super(message);
    }

    public TokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
