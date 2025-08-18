package com.likelion.cheongsanghoe.portfolio.api.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.likelion.cheongsanghoe.portfolio.domain.Portfolio;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PortfolioResponseDto {

    private Long portfolioId;
    private Long memberId;
    private String authorNickname;
    private String title;
    private String content;
    private String projectUrl;
    private String thumbnailUrl;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    public static PortfolioResponseDto from(Portfolio portfolio) {
        return PortfolioResponseDto.builder()
                .portfolioId(portfolio.getId())
                .memberId(portfolio.getMember().getId())
                .authorNickname(portfolio.getMember().getNickname())
                .title(portfolio.getTitle())
                .content(portfolio.getContent())
                .projectUrl(portfolio.getProjectUrl())
                .thumbnailUrl(portfolio.getThumbnailUrl())
                .createdAt(portfolio.getCreatedAt())
                .updatedAt(portfolio.getUpdatedAt())
                .build();
    }
}
