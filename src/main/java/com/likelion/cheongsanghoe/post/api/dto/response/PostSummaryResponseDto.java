package com.likelion.cheongsanghoe.post.api.dto.response;

import com.likelion.cheongsanghoe.post.domain.Post;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record PostSummaryResponseDto(
        Long post_id,
        String title,
        String location,
        int salary,
        LocalDate createAt
) {
    public static PostSummaryResponseDto from(Post post){
        return PostSummaryResponseDto.builder()
                .post_id(post.getPostId())
                .title(post.getTitle())
                .location(post.getLocation())
                .salary(post.getSalary())
                .createAt(post.getCreateAt())
                .build();

    }
}
