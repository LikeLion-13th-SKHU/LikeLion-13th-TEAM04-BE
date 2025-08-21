package com.likelion.cheongsanghoe.mainpage.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record MainPortfolioResDto(
        @Schema(description = "포트폴리오")
        long portfolio
) {
}
