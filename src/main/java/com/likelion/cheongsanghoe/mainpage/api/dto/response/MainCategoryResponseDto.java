package com.likelion.cheongsanghoe.mainpage.api.dto.response;

import com.likelion.cheongsanghoe.post.domain.Category;
import lombok.Builder;

@Builder
public record MainCategoryResponseDto(
        String name //카테고리 이름
) {
    //category enum 받아서 MainCategoryResDTO 생성
    public static MainCategoryResponseDto from(Category category) {
        return new MainCategoryResponseDto(category.toString());
    }
}
