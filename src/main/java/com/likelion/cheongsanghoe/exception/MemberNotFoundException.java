package com.likelion.cheongsanghoe.exception;

import com.likelion.cheongsanghoe.exception.status.ErrorStatus;

public class MemberNotFoundException extends CustomException {
    public MemberNotFoundException() {
        super(ErrorStatus.MEMBER_NOT_FOUND);
    }

    public MemberNotFoundException(String message) {
        super(ErrorStatus.MEMBER_NOT_FOUND, message);
    }

    public MemberNotFoundException(Long memberId) {
        super(ErrorStatus.MEMBER_NOT_FOUND, "Member not found with ID: " + memberId);
    }
}