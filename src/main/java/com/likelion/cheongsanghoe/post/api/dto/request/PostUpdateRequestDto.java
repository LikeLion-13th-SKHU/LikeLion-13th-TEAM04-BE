package com.likelion.cheongsanghoe.post.api.dto.request;

public record PostUpdateRequestDto(
        String title,
        int salary,
        int num,
        String work_time,
        String work_period,
        String content,
        String deadline
) {
}
