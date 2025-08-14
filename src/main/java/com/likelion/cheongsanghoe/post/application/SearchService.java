package com.likelion.cheongsanghoe.post.application;

import com.likelion.cheongsanghoe.post.domain.Category;
import com.likelion.cheongsanghoe.post.domain.Post;
import com.likelion.cheongsanghoe.post.domain.repository.PostRepository;
import com.likelion.cheongsanghoe.post.api.dto.response.PostSummaryResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchService {

    private final PostRepository postRepository;

    public Page<PostSummaryResponseDto> searchPosts(String keyword, Category category, Pageable pageable) {
        List<Post> allPosts = postRepository.findAll(); // 전체 조회

        // 검색(내용, 제목을 키워드로) + 카테고리 필터
        List<Post> filteredPosts = allPosts.stream()
                .filter(post -> {
                    boolean matchesKeyword = (keyword == null || keyword.isBlank()) ||
                            post.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                            post.getContent().toLowerCase().contains(keyword.toLowerCase());
                    boolean matchesCategory = (category == null) || post.getCategory() == category;
                    return matchesKeyword && matchesCategory;
                })
                .sorted((p1, p2) -> p2.getCreateAt().compareTo(p1.getCreateAt())) // 최신순 정렬
                .toList();

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filteredPosts.size());
        List<Post> pagedPosts = filteredPosts.subList(start, end);

        List<PostSummaryResponseDto> dtos = pagedPosts.stream()
                .map(PostSummaryResponseDto::from)
                .toList();

        return new org.springframework.data.domain.PageImpl<>(dtos, pageable, filteredPosts.size());
    }
}
