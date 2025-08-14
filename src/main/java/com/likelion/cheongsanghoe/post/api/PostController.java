package com.likelion.cheongsanghoe.post.api;

import com.likelion.cheongsanghoe.exception.Response;
import com.likelion.cheongsanghoe.exception.status.SuccessStatus;
import com.likelion.cheongsanghoe.global.api.dto.response.PaginationDto;
import com.likelion.cheongsanghoe.post.api.dto.request.PostUpdateRequestDto;
import com.likelion.cheongsanghoe.post.api.dto.response.*;
import com.likelion.cheongsanghoe.post.application.PostService;
import com.likelion.cheongsanghoe.post.api.dto.request.PostSaveRequestDto;
import com.likelion.cheongsanghoe.post.application.SearchService;
import com.likelion.cheongsanghoe.post.domain.Category;
import com.likelion.cheongsanghoe.post.domain.Post;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;
    private final SearchService searchService;

    //공고 생성
    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Response<Long>> postSave(@ModelAttribute @Valid PostSaveRequestDto postSaveRequestDto, @RequestHeader("userId") Long userId) {
        Response<Long> response = postService.postSave(postSaveRequestDto, userId);
        return ResponseEntity
                .status(SuccessStatus.CREATED.getStatus())
                .body(response);
    }

    //postId로 공고 상세 조회
    @GetMapping("/{postId}")
    public Response<PostInfoResponseDto> PostFindById(@PathVariable("postId")Long postId) {
        PostInfoResponseDto postInfoResponseDto = postService.getPostId(postId);
        return Response.success(SuccessStatus.SUCCESS,postInfoResponseDto);
    }
    //공고 전체 조회(요약 정보)
    @GetMapping
    public Response<PostPageResponseDto> postFindAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Category category,
            //PageableDefault로 페이지네이션 기본값 적용, pageable 파라미터 추가
            @PageableDefault(size = 10, sort = "createAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<PostSummaryResponseDto> postPageDto;
        //검색 조건이 있으면 검색 서비스 호출
        if ((keyword != null && !keyword.trim().isEmpty()) || category != null){
            postPageDto = searchService.searchPosts(keyword, category, pageable);
        }else{
            postPageDto = postService.getPostPage(pageable).map(PostSummaryResponseDto::from);
        }

        //PaginationDto 생성(페이지화 된 정보(postPageDto)를 가져온다)
        PaginationDto paginationDto = new PaginationDto(
                postPageDto.getNumber() + 1, //0부터 시작해서 +1
                postPageDto.getTotalPages(), //총 페이지수
                postPageDto.getTotalElements(), //총 데이터 항목 수
                postPageDto.getSize() //해당 페이지의 보여지는 항목 수
        );
        //게시글 리스트와 페이지네이션 정보를 합친다
        PostPageResponseDto postPageResponseDto = new PostPageResponseDto(postPageDto.getContent(), paginationDto);
        return Response.success(SuccessStatus.SUCCESS,postPageResponseDto);
    }
    //postId로 공고 수정
    @PatchMapping("/{postId}")
    public Response<PostInfoResponseDto> postUpdate(@PathVariable("postId")Long postId, @RequestBody PostUpdateRequestDto postUpdateRequestDto) {
        postService.postUpdate(postId, postUpdateRequestDto);
        PostInfoResponseDto postInfoResponseDto = postService.getPostId(postId);
        return Response.success(SuccessStatus.SUCCESS,postInfoResponseDto);
    }
    //공고 삭제
    @DeleteMapping("/{postId}")
    public Response<String> postDelete(@PathVariable("postId")Long postId) {
        postService.postDelete(postId);
        return Response.success(SuccessStatus.SUCCESS,null);
    }

}


