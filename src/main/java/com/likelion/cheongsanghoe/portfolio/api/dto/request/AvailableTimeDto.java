package com.likelion.cheongsanghoe.portfolio.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "업무 가능 시간 정보")
public class AvailableTimeDto {

    @Schema(description = "평일 가능 여부", example = "true")
    private Boolean weekday;

    @Schema(description = "주말 가능 여부", example = "false")
    private Boolean weekend;

    @Schema(description = "저녁시간 가능 여부", example = "true")
    private Boolean evening;

    @Schema(description = "유연한 시간 가능 여부", example = "true")
    private Boolean flexible;
}