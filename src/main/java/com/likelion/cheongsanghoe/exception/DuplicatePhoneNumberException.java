package com.likelion.cheongsanghoe.exception;

import com.likelion.cheongsanghoe.exception.status.ErrorStatus;

public class DuplicatePhoneNumberException extends CustomException {
    public DuplicatePhoneNumberException() {
        super(ErrorStatus.DUPLICATE_PHONE_NUMBER);
    }

    public DuplicatePhoneNumberException(String phoneNumber) {
        super(ErrorStatus.DUPLICATE_PHONE_NUMBER, "Phone number already exists: " + phoneNumber);
    }
}