package com.likelion.cheongsanghoe.mainpage.application;

import com.likelion.cheongsanghoe.mainpage.api.dto.response.MainCategoryResponseDto;
import com.likelion.cheongsanghoe.member.application.MemberService;
import com.likelion.cheongsanghoe.portfolio.api.dto.response.PortfolioResponseDto;
import com.likelion.cheongsanghoe.portfolio.application.PortfolioService;
import com.likelion.cheongsanghoe.post.api.dto.response.PostSummaryResponseDto;
import com.likelion.cheongsanghoe.post.application.PostService;
import com.likelion.cheongsanghoe.post.application.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MainService {
    private final PostService postService;
    private final SearchService searchService;
    private final MemberService memberService;
    private final PortfolioService portfolioService;

    //인기 카테고리
    public List<MainCategoryResponseDto> getMainCategory(int limit){
        return postService.getMainCategory(limit);
    }

    //최신 공고
    public List<PostSummaryResponseDto> getMainPost(int limit){
        return postService.getMainPost(limit);
    }

    //활동중인 청년 수
    public long getMainYouthCount(){
        return memberService.countActiveMembersWithRole();
    }

    //등록된 포트폴리오 수
    public long getMainPortfolioCount(){
        Pageable pageable = PageRequest.of(0, 1);

        Page<PortfolioResponseDto> allPortfolios = portfolioService.searchPortfolios(null, null, null, null, null, pageable);

        return allPortfolios.getTotalElements();
    }
}