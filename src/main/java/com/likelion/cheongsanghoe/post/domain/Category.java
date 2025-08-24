package com.likelion.cheongsanghoe.post.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.likelion.cheongsanghoe.exception.CustomException;
import com.likelion.cheongsanghoe.exception.status.ErrorStatus;

public enum Category {
    CAFE("카페"),
    RESTAURANT("음식점"),
    SUPERMARKET("마트"),
    LIFE("생활"),
    EDUCATION("교육"),
    CULTURE("문화"),
    ADD("기타"),
    ALL("전체조회");

    private final String label;

    Category(String label){
        this.label = label;
    }

    @Override
    public String toString(){
        return label;
    }


    @JsonCreator
    public static Category from(String input) {
        if (input == null) {
            throw new CustomException(ErrorStatus.INVALID_PARAMETER,
                    ErrorStatus.INVALID_BODY.getCode());
        }
        String trimmed = input.trim();

        for (Category c : values()) {
            if (c.name().equalsIgnoreCase(trimmed) || c.label.equalsIgnoreCase(trimmed)) {
                return c;
            }
        }

        // 잘못된 입력은 예외로 처리
        throw new CustomException(ErrorStatus.INVALID_PARAMETER,
                ErrorStatus.INVALID_PARAMETER.getCode());
    }

    //한글 라벨로 응답;
    @JsonValue
    public String toJson(){
        return this.label;
    }
}