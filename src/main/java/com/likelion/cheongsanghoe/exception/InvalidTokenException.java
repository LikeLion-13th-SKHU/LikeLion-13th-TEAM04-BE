package com.likelion.cheongsanghoe.exception;

import com.likelion.cheongsanghoe.exception.status.ErrorStatus;
import lombok.Getter;

@Getter
public class InvalidTokenException extends RuntimeException {

    private final ErrorStatus errorStatus;

    public InvalidTokenException(String message) {
        super(message);
        this.errorStatus = ErrorStatus.INVALID_TOKEN;
    }

    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
        this.errorStatus = ErrorStatus.INVALID_TOKEN;
    }
}
