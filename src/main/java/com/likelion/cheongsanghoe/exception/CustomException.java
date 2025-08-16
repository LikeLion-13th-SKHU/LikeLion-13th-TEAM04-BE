package com.likelion.cheongsanghoe.exception;


import com.likelion.cheongsanghoe.exception.status.ErrorStatus;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{
    private final ErrorStatus errorCode;

    public CustomException(ErrorStatus errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public CustomException(ErrorStatus errorCode, String detailMessage) {
        super(detailMessage);
        this.errorCode = errorCode;
    }
}
