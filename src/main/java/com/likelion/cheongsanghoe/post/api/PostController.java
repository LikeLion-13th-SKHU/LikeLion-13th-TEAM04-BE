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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
@Tag(name = "공고 CRUD")
public class PostController {
    private final PostService postService;
    private final SearchService searchService;

    //공고 생성
    @Operation(summary = "공고 생성")
    @PostMapping(value = "/save", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Response<Long>> postSave(@ModelAttribute @Valid PostSaveRequestDto postSaveRequestDto, HttpServletRequest request) {
        //순수한 토큰값만 분리 메서드(extractTokenFromHeader)
        String token = extractTokenFromHeader(request);
        Response<Long> response = postService.postSave(postSaveRequestDto, token);
        return ResponseEntity
                .status(SuccessStatus.POST_CREATED.getStatus())
                .body(response);
    }
    private String extractTokenFromHeader(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if(token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return null;
    }

    //postId로 공고 상세 조회
    @Operation(summary = "postId로 공고 상세 조회")
    @GetMapping("/{postId}")
    public Response<PostInfoResponseDto> PostFindById(@PathVariable("postId")Long postId) {
        PostInfoResponseDto postInfoResponseDto = postService.getPostId(postId);
        return Response.success(SuccessStatus.POST_SUCCESS,postInfoResponseDto);
    }
    //공고 전체 조회(요약 정보)
    @Operation(summary = "공고 전체 조회(요약된 공고 정보), 키워드랑 카테고리 쿼리 파라미터로 받는다(검색기능)")
    @GetMapping
    public Response<PostPageResponseDto> postFindAll(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Category category,
            //PageableDefault로 페이지네이션 기본값 적용, pageable 파라미터 추가
            @PageableDefault(size = 10, sort = "createAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<PostSummaryResponseDto> postPageDto = searchService.searchPosts(keyword, category, pageable);

        //PaginationDto 생성(페이지화 된 정보(postPageDto)를 가져온다)
        PaginationDto paginationDto = new PaginationDto(
                postPageDto.getNumber() + 1, //0부터 시작해서 +1
                postPageDto.getTotalPages(), //총 페이지수
                postPageDto.getTotalElements(), //총 데이터 항목 수
                postPageDto.getSize() //해당 페이지의 보여지는 항목 수
        );
        //게시글 리스트와 페이지네이션 정보를 합친다
        PostPageResponseDto postPageResponseDto = new PostPageResponseDto(postPageDto.getContent(), paginationDto);
        return Response.success(SuccessStatus.POST_SUCCESS,postPageResponseDto);
    }
    //postId로 공고 수정
    @Operation(summary = "postId로 공고 수정")
    @PatchMapping("/{postId}")
    public Response<PostInfoResponseDto> postUpdate(@PathVariable("postId")Long postId, @RequestBody PostUpdateRequestDto postUpdateRequestDto) {
        postService.postUpdate(postId, postUpdateRequestDto);
        PostInfoResponseDto postInfoResponseDto = postService.getPostId(postId);
        return Response.success(SuccessStatus.POST_UPDATED,postInfoResponseDto);
    }
    //공고 삭제
    @Operation(summary = "공고 삭제")
    @DeleteMapping("/{postId}")
    public Response<String> postDelete(@PathVariable("postId")Long postId) {
        postService.postDelete(postId);
        return Response.success(SuccessStatus.POST_DELETE,null);
    }

}


