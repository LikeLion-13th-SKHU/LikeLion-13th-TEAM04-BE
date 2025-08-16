package com.likelion.cheongsanghoe.post.api.dto.response;

import com.likelion.cheongsanghoe.post.domain.Category;
import lombok.Builder;

@Builder
public record CategoryCountDto(
        //카테고리별 카운트
        Category category,
        long count
) {
}
