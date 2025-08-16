package com.likelion.cheongsanghoe.post.api.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record PostListResponseDto_Summary(
        List<PostSummaryResponseDto> posts //요약 정보 DTO
) {
    public static PostListResponseDto_Summary from(List<PostSummaryResponseDto> posts) {
        return PostListResponseDto_Summary.builder()
                .posts(posts)
                .build();
    }
}
