package com.likelion.cheongsanghoe.post.api.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record PostListResponseDto_Detail(
        List<PostInfoResponseDto> posts
) {
    public static PostListResponseDto_Detail from(List<PostInfoResponseDto> posts){
        return PostListResponseDto_Detail.builder()
                .posts(posts)
                .build();
    }
}
