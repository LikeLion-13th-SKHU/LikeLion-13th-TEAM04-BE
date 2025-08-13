package com.likelion.cheongsanghoe.post.api;

import com.likelion.cheongsanghoe.exception.Response;
import com.likelion.cheongsanghoe.exception.status.SuccessStatus;
import com.likelion.cheongsanghoe.global.api.dto.response.PaginationDto;
import com.likelion.cheongsanghoe.post.api.dto.request.PostUpdateRequestDto;
import com.likelion.cheongsanghoe.post.api.dto.response.*;
import com.likelion.cheongsanghoe.post.application.PostService;
import com.likelion.cheongsanghoe.post.api.dto.request.PostSaveRequestDto;
import com.likelion.cheongsanghoe.post.domain.Post;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    //공고 생성
    @PostMapping("/save")
    public ResponseEntity<Response<Long>> postSave(@RequestBody @Valid PostSaveRequestDto postSaveRequestDto, @RequestHeader("userId") Long userId) {
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
    public Response<PostPageResponseDto> PostFindAll(
            //PageableDefault로 페이지네이션 기본값 적용
            @PageableDefault(size = 10, sort = "postId", direction = Sort.Direction.ASC) Pageable pageable) {//Pageable 파라미터 추가
        Page<Post> postPage = postService.getPostPage(pageable);//페이지네이션 된 결과를 가져온다
        //Post 엔티티를 PostSummaryResponseDto로 변환
        List<PostSummaryResponseDto> posts = postPage.getContent().stream()
                .map(PostSummaryResponseDto::from)
                .collect(Collectors.toList());
        //PaginationDto 생성(페이지화 된 정보(postPage)를 가져온다)
        PaginationDto paginationDto = new PaginationDto(
                postPage.getNumber() + 1, //0부터 시작해서 +1
                postPage.getTotalPages(), //총 페이지수
                postPage.getTotalElements(), //총 데이터 항목 수
                postPage.getSize() //해당 페이지의 보여지는 항목 수
        );
        //게시글 리스트와 페이지네이션 정보를 합친다
        PostPageResponseDto postPageResponseDto = new PostPageResponseDto(posts, paginationDto);
        return Response.success(SuccessStatus.SUCCESS,postPageResponseDto);
    }
    //postId로 공고 수정
    @PatchMapping("/{postId}")
    public Response<String> postUpdate(@PathVariable("postId")Long postId, @RequestBody PostUpdateRequestDto postUpdateRequestDto) {
        postService.postUpdate(postId, postUpdateRequestDto);
        return Response.success(SuccessStatus.SUCCESS,null);
    }
    //공고 삭제
    @DeleteMapping("/{postId}")
    public Response<String> postDelete(@PathVariable("postId")Long postId) {
        postService.postDelete(postId);
        return Response.success(SuccessStatus.SUCCESS,null);
    }

}
