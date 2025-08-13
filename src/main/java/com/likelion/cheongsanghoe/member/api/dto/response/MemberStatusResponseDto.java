package com.likelion.cheongsanghoe.member.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberStatusResponseDto {

    private boolean success;
    private String message;

    public static MemberStatusResponseDto success(String message) {
        return new MemberStatusResponseDto(true, message);
    }

    public static MemberStatusResponseDto failure(String message) {
        return new MemberStatusResponseDto(false, message);
    }
}