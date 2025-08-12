package com.likelion.cheongsanghoe.auth.api.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponseDto {
    private String accessToken;
    private boolean isNewUser;
    private boolean hasRole;
    private AuthResponseDto user;
}