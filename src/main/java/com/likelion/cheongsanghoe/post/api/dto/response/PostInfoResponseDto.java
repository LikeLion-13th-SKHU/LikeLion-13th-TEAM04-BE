package com.likelion.cheongsanghoe.post.api.dto.response;

import com.likelion.cheongsanghoe.post.domain.Category;
import com.likelion.cheongsanghoe.post.domain.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record PostInfoResponseDto(

        @Schema(description = "공고 제목", example = "공고 제목")
        String title,

        @Schema(description = "공고 내용", example = "내용")
        String content,

        @Schema(description = "해당 위치", example = "위치")
        String location,

        @Schema(description = "급여", example = "11000")
        Integer salary,

        @Schema(description = "일하는 시간", example = "11:00~13:00")
        String work_time,

        @Schema(description = "해당 공고에 원하는 태그", example = "책임감, 경력무관")
        String tags,

        @Schema(description = "공고 마감일", example = "2025.07.25")
        String deadline,

        @Schema(description = "모집 인원", example = "1")
        int num,

        @Schema(description = "일하는 기간", example = "2025.07.27~2025.07.28")
        String work_period,

        @Schema(description = "공고 생성일", example = "2025.07.20")
        LocalDate createAt,

        @Schema(description = "카테고리", example = "카페")
        Category category,

        @Schema(description = "현재 로그인한 사용자가 이 공고의 작성자인지 여부", example = "true")
        boolean isUser,

        @Schema(description = "공고 작성한 사용자 ID", example = "1")
        Long postUserId,

        @Schema(description = "이미지 URL", example = "/uploads/uuid.jpg")
        String imageUrl
) {
    public static PostInfoResponseDto from(Post post, boolean isUser){
        return PostInfoResponseDto.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .location(post.getLocation())
                .salary(post.getSalary())
                .work_time(post.getWork_time())
                .tags(post.getTags())
                .deadline(post.getDeadline())
                .num(post.getNum())
                .work_period(post.getWork_period())
                .createAt(post.getCreateAt())
                .category(post.getCategory())
                .isUser(isUser)
                .postUserId(post.getUser() != null ? post.getUser().getId() : null)
                .imageUrl(post.getImageUrl())
                .build();
    }
}
