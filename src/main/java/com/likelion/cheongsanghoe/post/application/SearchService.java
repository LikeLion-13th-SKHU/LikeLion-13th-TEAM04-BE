package com.likelion.cheongsanghoe.post.application;

import com.likelion.cheongsanghoe.post.domain.Category;
import com.likelion.cheongsanghoe.post.domain.Post;
import com.likelion.cheongsanghoe.post.domain.repository.PostRepository;
import com.likelion.cheongsanghoe.post.api.dto.response.PostSummaryResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.parameters.P;
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
        // category가 null이거나 ALL인 경우, 실제 검색 쿼리에는 null을 전달하여 모든 카테고리를 조회하도록 함
        Category searchCategory = (category == null || category == Category.ALL) ? null : category;

        // keyword도 null이거나 비어있으면 전체 검색으로 동작
        Page<Post> allPosts;
        if (searchCategory == null) {
            allPosts = postRepository.findKeyword(keyword, pageable);
        } else {
            allPosts = postRepository.findKeywordAndCategory(keyword, searchCategory, pageable);
        }

        //DB에서 페이징된 Post 객체들을 Dto로 매핑해서 반환
        return allPosts.map(PostSummaryResponseDto::from);
    }
}
