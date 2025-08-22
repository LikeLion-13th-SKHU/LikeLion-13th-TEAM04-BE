package com.likelion.cheongsanghoe.portfolio.api.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.likelion.cheongsanghoe.portfolio.api.dto.request.AvailableTimeDto;
import com.likelion.cheongsanghoe.portfolio.domain.Portfolio;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "포트폴리오 응답 DTO")
public class PortfolioResponseDto {

    @Schema(description = "포트폴리오 ID", example = "1")
    private Long portfolioId;

    @Schema(description = "작성자 회원 ID", example = "123")
    private Long memberId;

    @Schema(description = "작성자 닉네임", example = "디자이너김철수")
    private String authorNickname;

    @Schema(description = "포트폴리오 제목", example = "웹 디자인 포트폴리오")
    private String title;

    @Schema(description = "포트폴리오 내용", example = "React와 TypeScript를 활용한 포트폴리오 사이트입니다.")
    private String content;

    @Schema(description = "프로젝트 URL", example = "https://github.com/username/project")
    private String projectUrl;

    @Schema(description = "썸네일 이미지 URL", example = "https://example.com/thumbnail.jpg")
    private String thumbnailUrl;

    @Schema(description = "업무 분야", example = "영상/편집")
    private String category;

    @Schema(description = "보유 기술/재능", example = "Premiere Pro, After Effects, Photoshop")
    private String skills;

    @Schema(description = "경험 수준", example = "중급자")
    private String experience;

    @Schema(description = "시급/건당 요금", example = "시급 15,000원")
    private String hourlyRate;

    @Schema(description = "업무 가능 시간")
    private AvailableTimeDto availableTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "생성일시", example = "2024-03-15T10:30:00")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "수정일시", example = "2024-03-15T10:30:00")
    private LocalDateTime updatedAt;

    public static PortfolioResponseDto from(Portfolio portfolio) {
        AvailableTimeDto availableTimeDto = null;
        if (portfolio.getAvailableTime() != null) {
            availableTimeDto = AvailableTimeDto.builder()
                    .weekday(portfolio.getAvailableTime().getWeekday())
                    .weekend(portfolio.getAvailableTime().getWeekend())
                    .evening(portfolio.getAvailableTime().getEvening())
                    .flexible(portfolio.getAvailableTime().getFlexible())
                    .build();
        }

        return PortfolioResponseDto.builder()
                .portfolioId(portfolio.getId())
                .memberId(portfolio.getMember().getId())
                .authorNickname(portfolio.getMember().getNickname())
                .title(portfolio.getTitle())
                .content(portfolio.getContent())
                .projectUrl(portfolio.getProjectUrl())
                .thumbnailUrl(portfolio.getThumbnailUrl())
                .category(portfolio.getCategory())
                .skills(portfolio.getSkills())
                .experience(portfolio.getExperience())
                .hourlyRate(portfolio.getHourlyRate())
                .availableTime(availableTimeDto)
                .createdAt(portfolio.getCreatedAt())
                .updatedAt(portfolio.getUpdatedAt())
                .build();
    }
}