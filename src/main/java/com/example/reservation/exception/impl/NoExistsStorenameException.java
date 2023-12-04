package com.example.reservation.exception.impl;

import com.example.reservation.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class NoExistsStorenameException extends AbstractException {
    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }

    @Override
    public String getMessage() {
        return "존재하지 않는 매장 입니다.";
    }
}
