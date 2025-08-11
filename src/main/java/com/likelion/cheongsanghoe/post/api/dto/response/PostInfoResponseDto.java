package com.likelion.cheongsanghoe.post.api.dto.response;

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
        int count,
        String work_period,
        LocalDate create_at,
        String category
) {
    public static PostInfoResponseDto from(Post post){
        return PostInfoResponseDto.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .location(post.getLocation())
                .salary(post.getSalary())
                .work_time(post.getWork_time())
                .deadline(post.getDeadline())
                .count(post.getCount())
                .work_period(post.getWork_period())
                .create_at(post.getCreate_at())
                .category(post.getCategory().getName())
                .build();
    }
}
