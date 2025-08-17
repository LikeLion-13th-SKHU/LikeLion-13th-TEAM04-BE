package com.likelion.cheongsanghoe.post.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
public record PostListResponseDto_Detail(
        @Schema(description = "공고 상세 정보 리스트")
        List<PostInfoResponseDto> posts
) {
    public static PostListResponseDto_Detail from(List<PostInfoResponseDto> posts){
        return PostListResponseDto_Detail.builder()
                .posts(posts)
                .build();
    }
}
