package com.likelion.cheongsanghoe.post.api.dto.response;

import com.likelion.cheongsanghoe.post.domain.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record CategoryCountDto(
        //카테고리별 카운트
        @Schema(description = "카테고리")
        Category category,

        @Schema(description = "카테고리별 개수")
        long count
) {
}
