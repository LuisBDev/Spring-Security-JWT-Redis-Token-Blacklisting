package com.luis.spring.security.msauth_poc.exception;

/**
 * @author Luis Balarezo
 **/
public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException(String email) {
        super(String.format("Email '%s' is already registered", email));
    }
}
