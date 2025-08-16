package com.likelion.cheongsanghoe.post.api.dto.response;

import com.likelion.cheongsanghoe.global.api.dto.response.PaginationDto;

import java.util.List;

public record PostPageResponseDto(
        List<PostSummaryResponseDto> posts,
        PaginationDto pagination
) {
}
