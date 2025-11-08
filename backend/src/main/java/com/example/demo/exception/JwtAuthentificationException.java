package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@REsponseStatus(HttpStatus.UNAUTHORIZED)
public class JwtAuthentificationException extends RuntimeException {
    public JwtAuthentificationException(String message) {
        super(message);
    }
}
