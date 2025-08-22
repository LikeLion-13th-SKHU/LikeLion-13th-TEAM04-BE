package com.likelion.cheongsanghoe.mainpage.api.dto;


import com.likelion.cheongsanghoe.exception.Response;
import com.likelion.cheongsanghoe.exception.status.SuccessStatus;
import com.likelion.cheongsanghoe.mainpage.api.dto.response.MainCategoryResponseDto;
import com.likelion.cheongsanghoe.mainpage.api.dto.response.MainMemberResDto;
import com.likelion.cheongsanghoe.mainpage.api.dto.response.MainPortfolioResDto;
import com.likelion.cheongsanghoe.mainpage.application.MainService;
import com.likelion.cheongsanghoe.post.api.dto.response.PostPageResponseDto;
import com.likelion.cheongsanghoe.post.api.dto.response.PostSummaryResponseDto;
import com.likelion.cheongsanghoe.post.application.PostService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/main")
public class MainController {
    private final MainService mainService;

    //인기 카테고리
    @Operation(summary = "메인 페이지에 인기 카테고리 4개 조회")
    @GetMapping("/category")
    public Response<List<MainCategoryResponseDto>> getMainCategory(@RequestParam(defaultValue = "4") int limit){
        List<MainCategoryResponseDto> mainCategoryResponseDto = mainService.getMainCategory(limit);
        return Response.success(SuccessStatus.SUCCESS, mainCategoryResponseDto);
    }

    //최신 공고
    @Operation(summary = "메인 페이지에 최신 공고 2개 조회")
    @GetMapping("/post")
    public Response<List<PostSummaryResponseDto>> getMainPost(@RequestParam(defaultValue = "2") int limit){
        List<PostSummaryResponseDto> mainPostResponseDto = mainService.getMainPost(limit);
        return Response.success(SuccessStatus.SUCCESS, mainPostResponseDto);
    }

    //등록된 포폴 수
    @Operation(summary = "메인페이지 등록된 포트폴리오 전체 수")
    @GetMapping("/portfolio")
    public Response<MainPortfolioResDto> getMainPortfolioCount(){
        long count = mainService.getMainPortfolioCount();
        MainPortfolioResDto mainPortfolioResDto = MainPortfolioResDto.builder()
                .portfolio(count)
                .build();
        return Response.success(SuccessStatus.SUCCESS, mainPortfolioResDto);
    }

    //활동중인 청년
    @Operation(summary = "메인페이지 활동중인 청년 수")
    @GetMapping("/member")
    public Response<MainMemberResDto> getMainMemberCount(){
        long count = mainService.getMainYouthCount();
        MainMemberResDto mainMemberResDto = MainMemberResDto.builder()
                .youth(count)
                .build();
        return Response.success(SuccessStatus.SUCCESS, mainMemberResDto);
    }

}
