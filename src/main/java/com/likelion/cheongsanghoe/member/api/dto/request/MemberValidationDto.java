package com.likelion.cheongsanghoe.member.api.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberValidationDto {

    private String nickname;
    private String phoneNumber;

    public MemberValidationDto(String nickname, String phoneNumber) {
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
    }
}