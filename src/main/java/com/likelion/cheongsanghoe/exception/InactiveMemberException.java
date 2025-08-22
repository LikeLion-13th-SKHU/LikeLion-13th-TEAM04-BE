package com.likelion.cheongsanghoe.exception;

public class InactiveMemberException extends RuntimeException {
    public InactiveMemberException(String message) {
        super(message);
    }

    public InactiveMemberException(Long memberId) {
        super("비활성화된 회원입니다. memberId=" + memberId);
    }
}
