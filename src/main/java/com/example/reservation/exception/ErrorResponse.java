package com.example.reservation.exception;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

//에러가 발생했을때 던져줄 모델 틀래스
@Data
@Builder
public class ErrorResponse {
    private int code;
    private String message;
}
