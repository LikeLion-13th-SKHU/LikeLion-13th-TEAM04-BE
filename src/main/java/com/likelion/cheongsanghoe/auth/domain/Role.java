package com.likelion.cheongsanghoe.auth.domain;

public enum Role {
    MERCHANT("상인"),
    YOUTH("청년");
//상인 청년 번역을 어떻게 할지 잘 모르겠어서 일단은 이렇게 했어요 바꾸고 싶으면 말씀해 주세요.

    private final String description;

    Role(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}