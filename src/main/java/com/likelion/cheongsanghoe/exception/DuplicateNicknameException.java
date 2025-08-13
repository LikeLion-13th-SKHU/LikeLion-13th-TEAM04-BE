package com.likelion.cheongsanghoe.exception;

import com.likelion.cheongsanghoe.exception.status.ErrorStatus;

public class DuplicateNicknameException extends CustomException {
    public DuplicateNicknameException() {
        super(ErrorStatus.DUPLICATE_NICKNAME);
    }

    public DuplicateNicknameException(String nickname) {
        super(ErrorStatus.DUPLICATE_NICKNAME, "Nickname already exists: " + nickname);
    }
}