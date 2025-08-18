package com.likelion.cheongsanghoe.mainpage.api.dto;


import com.likelion.cheongsanghoe.exception.Response;
import com.likelion.cheongsanghoe.exception.status.SuccessStatus;
import com.likelion.cheongsanghoe.mainpage.api.dto.response.MainCategoryResponseDto;
import com.likelion.cheongsanghoe.mainpage.application.MainService;
import com.likelion.cheongsanghoe.post.api.dto.response.PostPageResponseDto;
import com.likelion.cheongsanghoe.post.api.dto.response.PostSummaryResponseDto;
import com.likelion.cheongsanghoe.post.application.PostService;
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
    @GetMapping("/category")
    public Response<List<MainCategoryResponseDto>> getMainCategory(@RequestParam(defaultValue = "4") int limit){
        List<MainCategoryResponseDto> mainCategoryResponseDto = mainService.getMainCategory(limit);
        return Response.success(SuccessStatus.SUCCESS, mainCategoryResponseDto);
    }

    //최신 공고
    @GetMapping("/post")
    public Response<List<PostSummaryResponseDto>> getMainPost(@RequestParam(defaultValue = "2") int limit){
        List<PostSummaryResponseDto> mainPostResponseDto = mainService.getMainPost(limit);
        return Response.success(SuccessStatus.SUCCESS, mainPostResponseDto);
    }

}
