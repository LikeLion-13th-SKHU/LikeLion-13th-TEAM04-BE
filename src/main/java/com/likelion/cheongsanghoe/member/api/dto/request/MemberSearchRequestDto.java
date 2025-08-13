package com.likelion.cheongsanghoe.member.api.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberSearchRequestDto {

    private String keyword; // 닉네임 검색
    private String address; // 지역 검색
    private String role;


    public MemberSearchRequestDto(String keyword, String address, String role) {
        this.keyword = keyword;
        this.address = address;
        this.role = role;
    }
}