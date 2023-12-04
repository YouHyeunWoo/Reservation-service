package com.example.reservation.exception.impl;

import com.example.reservation.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class NoMatchPasswordException extends AbstractException {
    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }

    @Override
    public String getMessage() {
        return "잘못된 비밀번호 입니다.";
    }
}
