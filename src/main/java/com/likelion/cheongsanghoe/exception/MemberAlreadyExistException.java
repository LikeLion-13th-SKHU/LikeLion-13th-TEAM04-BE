package com.likelion.cheongsanghoe.exception;

import com.likelion.cheongsanghoe.exception.status.ErrorStatus;

public class MemberAlreadyExistException extends CustomException {
    public MemberAlreadyExistException() {
        super(ErrorStatus.MEMBER_ALREADY_EXISTS);
    }

    public MemberAlreadyExistException(String detail) {
        super(ErrorStatus.MEMBER_ALREADY_EXISTS, detail);
    }
}