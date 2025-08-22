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
        Page<Post> allPosts = postRepository.findKeywordAndCategory(keyword, category, pageable); // 검색한 부분 전체 조회

        //DB에서 페이징된 Post 객체들을 Dto로 매핑해서 반환
        return allPosts.map(PostSummaryResponseDto::from);

    }
}
