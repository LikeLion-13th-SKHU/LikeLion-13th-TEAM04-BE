package com.likelion.cheongsanghoe.mainpage.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record MainMemberResDto(
        @Schema(description = "청년")
        long youth
) {
}
