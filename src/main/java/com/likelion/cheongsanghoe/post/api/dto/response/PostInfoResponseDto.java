package com.likelion.cheongsanghoe.post.api.dto.response;

import com.likelion.cheongsanghoe.post.domain.Category;
import com.likelion.cheongsanghoe.post.domain.Post;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record PostInfoResponseDto(
        String title,
        String content,
        String location,
        int salary,
        String work_time,
        String deadline,
        int num,
        String work_period,
        LocalDate createAt,
        Category category
) {
    public static PostInfoResponseDto from(Post post){
        return PostInfoResponseDto.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .location(post.getLocation())
                .salary(post.getSalary())
                .work_time(post.getWork_time())
                .deadline(post.getDeadline())
                .num(post.getNum())
                .work_period(post.getWork_period())
                .createAt(post.getCreateAt())
                .category(post.getCategory())
                .build();
    }
}
