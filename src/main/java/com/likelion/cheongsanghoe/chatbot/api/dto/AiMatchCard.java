package com.likelion.cheongsanghoe.chatbot.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record AiMatchCard(

        @Schema(description = "후보 ID", example = "28")
        String id,

        @Schema(description = "이름", example = "정다운")
        String name,

        @Schema(description = "프로필 요약", example = "백엔드 개발, 서울 노원구, 평일 오후, spring")
        String profile,

        @Schema(description = "매칭 점수", example = "93")
        Integer score,

        @Schema(description = "구분(청년/상인 등)", example = "청년")
        String type,

        @Schema(description = "성별", example = "여성")
        String gender,

        @Schema(description = "직무", example = "백엔드 개발")
        String job,

        @Schema(description = "보유 기술", example = "백엔드 개발")
        String skills

) {
}
