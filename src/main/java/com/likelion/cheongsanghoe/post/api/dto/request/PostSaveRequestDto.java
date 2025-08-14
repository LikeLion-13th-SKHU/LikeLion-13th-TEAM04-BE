package com.likelion.cheongsanghoe.post.api.dto.request;

import com.likelion.cheongsanghoe.post.domain.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

public record PostSaveRequestDto(
        @NotNull(message = "카테고리를 선택하세요")
        Category category,
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
        int num,
        @NotBlank(message = "일하는 기간을 입력하세요")
        String work_period,
        String tags,
        MultipartFile image,
        LocalDate createAt
) {
}
