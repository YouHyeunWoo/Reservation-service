package com.example.reservation.exception.impl;

import com.example.reservation.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class NoExistsReviewException extends AbstractException {
    @Override
    public int getStatusCode() {
        return HttpStatus.OK.value();
    }

    @Override
    public String getMessage() {
        return "작성된 리뷰가 없습니다.";
    }
}
