package com.likelion.cheongsanghoe.post.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record PostUpdateRequestDto(

        @Schema(description = "공고 수정 제목", example = "수정된 제목")
        String title,

        @Schema(description = "급여", example = "수정된 급여")
        int salary,

        @Schema(description = "모집 인원", example = "수정 모집인원")
        int num,

        @Schema(description = "일하는 시간대", example = "수정 일하는 시간")
        String work_time,

        @Schema(description = "일하는 기간", example = "수정 일하는 기간")
        String work_period,

        @Schema(description = "공고 내용", example = "수정 내용")
        String content,

        @Schema(description = "공고 마감일", example = "수정 마감일")
        String deadline
) {
}
