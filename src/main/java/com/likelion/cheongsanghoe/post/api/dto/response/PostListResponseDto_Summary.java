package com.likelion.cheongsanghoe.post.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
public record PostListResponseDto_Summary(
        @Schema(description = "공고 요약정보 리스트")
        List<PostSummaryResponseDto> posts //요약 정보 DTO
) {
    public static PostListResponseDto_Summary from(List<PostSummaryResponseDto> posts) {
        return PostListResponseDto_Summary.builder()
                .posts(posts)
                .build();
    }
}
