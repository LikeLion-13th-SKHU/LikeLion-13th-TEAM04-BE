package com.likelion.cheongsanghoe.portfolio.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
@Schema(description = "포트폴리오 생성 요청 DTO")
public class PortfolioCreateRequestDto {

    @NotBlank(message = "제목은 필수 입력 항목입니다.")
    @Schema(description = "포트폴리오 제목", example = "웹 디자인 포트폴리오", required = true)
    private String title;

    @NotBlank(message = "내용은 필수 입력 항목입니다.")
    @Schema(description = "포트폴리오 내용", example = "React와 TypeScript를 활용한 포트폴리오 사이트입니다.", required = true)
    private String content;

    @Schema(description = "프로젝트 URL", example = "https://github.com/username/project")
    private String projectUrl;

    @Schema(description = "썸네일 이미지 URL", example = "https://example.com/thumbnail.jpg")
    private String thumbnailUrl;

    @Schema(description = "업무 분야", example = "영상/편집")
    private String category;

    @Schema(description = "보유 기술/재능", example = "Premiere Pro, After Effects, Photoshop")
    private String skills;

    @Schema(description = "경험 수준", example = "중급자", allowableValues = {"초보자", "중급자", "고급자", "전문가"})
    private String experience;

    @Schema(description = "시급/건당 요금", example = "시급 15,000원")
    private String hourlyRate;

    @Valid
    @Schema(description = "업무 가능 시간")
    private AvailableTimeDto availableTime;
}