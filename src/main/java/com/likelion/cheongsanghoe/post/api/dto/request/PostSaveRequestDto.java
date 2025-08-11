package com.likelion.cheongsanghoe.post.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PostSaveRequestDto(
        @NotNull(message = "카테고리를 필수로 입력하세요")
        Long categoryId,
        @NotBlank (message = "제목을 입력하세요")
        String title,
        @NotBlank(message = "내용을 입력하세요")
        @Size(min = 10, max = 300)
        String content,
        @NotBlank(message = "위치를 입력하세요")
        String location,
        int salary,
        @NotBlank(message = "일하는 시간을 입력하세요")
        String work_time,
        String deadline,
        int count,
        @NotBlank(message = "일하는 기간을 입력하세요")
        String work_period,
        LocalDate create_at
) {
}
