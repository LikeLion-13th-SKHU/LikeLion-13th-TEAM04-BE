package com.likelion.cheongsanghoe.mainpage.application;

import com.likelion.cheongsanghoe.mainpage.api.dto.response.MainCategoryResponseDto;
import com.likelion.cheongsanghoe.post.application.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MainService {
    private final PostService postService;

    //인기 카테고리
    public List<MainCategoryResponseDto> getMainCategory(int limit){
        return postService.getMainCategory(limit);
    }
}
