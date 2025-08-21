package com.likelion.cheongsanghoe.post.api.dto.request;

import com.likelion.cheongsanghoe.post.domain.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

public record PostSaveRequestDto(
        @Schema(description = "카테고리", example = "카페")
        @NotNull(message = "카테고리를 선택하세요")
        Category category,

        @Schema(description = "공고 제목", example = "30초 영상편집자 구합니다")
        @NotBlank (message = "제목을 입력하세요")
        String title,

        @Schema(description = "공고 내용", example = "저희 가게에서 30초 숏폼을 만들어 홍보 영상을 만들려고 합니다.")
        @NotBlank(message = "내용을 입력하세요")
        @Size(min = 10, max = 300)
        String content,

        @Schema(description = "위치", example = "서울시 구로구")
        @NotBlank(message = "위치를 입력하세요")
        String location,

        @Schema(description = "급여", example = "11000")
        int salary,

        @Schema(description = "일하는 시간", example = "11:00~13:00")
        @NotBlank(message = "일하는 시간을 입력하세요")
        String work_time,

        @Schema(description = "공고 마감일", example = "2025.07.25")
        String deadline,

        @Schema(description = "모집 인원", example = "1")
        int num,

        @Schema(description = "일하는 기간", example = "2025.07.27~2025.07.28")
        @NotBlank(message = "일하는 기간을 입력하세요")
        String work_period,

        @Schema(description = "해당 공고에 원하는 태그", example = "책임감, 경력무관")
        String tags,

        @Schema(description = "이미지", example = "사진URL")
        MultipartFile image,

        @Schema(description = "공고 생성일", example = "2025.07.20")
        LocalDate createAt
) {
}
