package com.likelion.cheongsanghoe.global.api.dto.response;

import lombok.Builder;

@Builder
public record PaginationDto(
        int currentPage, //현재 페이지
        int totalPages, //전체 페이지 수
        long totalCount, //총 데이터의 항목 수
        int pageSize //해당 페이지에 항목 수
) {

}
