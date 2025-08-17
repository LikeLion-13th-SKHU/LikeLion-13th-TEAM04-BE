package com.likelion.cheongsanghoe.post.api.dto.response;

import com.likelion.cheongsanghoe.post.domain.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Builder
public record PostSummaryResponseDto(

        @Schema(description = "공고 id")
        Long post_id,

        @Schema(description = "공고 제목", example = "공고 제목")
        String title,

        @Schema(description = "해당 위치", example = "위치")
        String location,

        @Schema(description = "급여", example = "11000")
        Integer salary,

        @Schema(description = "해당 공고에 원하는 태그", example = "책임감, 경력무관")
        List<String> tags,

        @Schema(description = "공고 생성일", example = "2025.07.20")
        LocalDate createAt
) {
    public static PostSummaryResponseDto from(Post post){

        List<String> parsedTags = (post.getTags() != null && !post.getTags().trim().isEmpty())?
                Arrays.stream(post.getTags().split(",")) //콤마로 나누고
                        .map(String::trim) //각 태그 문자열 공백 제거
                        .filter(tag -> !tag.isEmpty()) //빈 태그 제거
                        .collect(Collectors.toList()) : List.of(); //태그가 null이거나 비었으면 빈 리스트 반환
        return PostSummaryResponseDto.builder()
                .post_id(post.getPostId()) //메인 페이지 데이터
                .title(post.getTitle()) //메인 페이지 데이터
                .location(post.getLocation())
                .salary(post.getSalary())//메인 페이지 데이터
                .tags(parsedTags) //메인 페이지 데이터
                .createAt(post.getCreateAt())
                .build();

    }
}
