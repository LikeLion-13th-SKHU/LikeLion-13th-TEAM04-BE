package com.likelion.cheongsanghoe.portfolio.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "포트폴리오 수정 요청 DTO")
public class PortfolioUpdateRequestDto {

    @Size(max = 100, message = "제목은 100자 이내로 작성해주세요")
    @Schema(description = "포트폴리오 제목", example = "웹 디자인 포트폴리오")
    private String title;

    @Schema(description = "포트폴리오 내용", example = "React와 TypeScript를 활용한 포트폴리오 사이트입니다.")
    private String content;

    @Size(max = 255, message = "프로젝트 URL은 255자 이내로 작성해주세요")
    @Schema(description = "프로젝트 URL", example = "https://github.com/username/project")
    private String projectUrl;

    @Size(max = 255, message = "썸네일 URL은 255자 이내로 작성해주세요")
    @Schema(description = "썸네일 이미지 URL", example = "https://example.com/thumbnail.jpg")
    private String thumbnailUrl;

    @Size(max = 100, message = "업무 분야는 100자 이내로 작성해주세요")
    @Schema(description = "업무 분야", example = "영상/편집")
    private String category;

    @Size(max = 500, message = "보유 기술은 500자 이내로 작성해주세요")
    @Schema(description = "보유 기술/재능", example = "Premiere Pro, After Effects, Photoshop")
    private String skills;

    @Size(max = 50, message = "경험 수준은 50자 이내로 작성해주세요")
    @Schema(description = "경험 수준", example = "중급자", allowableValues = {"초보자", "중급자", "고급자", "전문가"})
    private String experience;

    @Size(max = 100, message = "시급/요금 정보는 100자 이내로 작성해주세요")
    @Schema(description = "시급/건당 요금", example = "시급 15,000원")
    private String hourlyRate;

    @Valid
    @Schema(description = "업무 가능 시간")
    private AvailableTimeDto availableTime;
}