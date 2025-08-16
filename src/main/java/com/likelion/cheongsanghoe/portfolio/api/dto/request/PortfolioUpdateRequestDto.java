package com.likelion.cheongsanghoe.portfolio.api.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class PortfolioUpdateRequestDto {

    @Size(max = 100, message = "제목은 100자 이내로 작성해주세요")
    private String title;

    private String content;

    @Size(max = 255, message = "프로젝트 URL은 255자 이내로 작성해주세요")
    private String projectUrl;

    @Size(max = 255, message = "썸네일 URL은 255자 이내로 작성해주세요")
    private String thumbnailUrl;
}