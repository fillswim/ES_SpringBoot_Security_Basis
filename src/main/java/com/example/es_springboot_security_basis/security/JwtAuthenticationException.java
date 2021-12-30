package com.example.es_springboot_security_basis.security;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;


@Getter
public class JwtAuthenticationException extends AuthenticationException {

    private HttpStatus httpStatus;

    public JwtAuthenticationException(String msg) {
        super(msg);
    }

    public JwtAuthenticationException(String explanation, HttpStatus httpStatus) {
        super(explanation);
        this.httpStatus = httpStatus;
    }

}
