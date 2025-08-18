package com.likelion.cheongsanghoe.auth.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "사용자 역할 선택 요청")
public class RoleSelectionRequestDto {

    @Schema(description = "사용자 역할", example = "YOUTH", allowableValues = {"YOUTH", "MERCHANT"}, required = true)
    private String role; // 상인 청년
}