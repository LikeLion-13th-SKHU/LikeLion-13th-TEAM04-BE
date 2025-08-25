package com.likelion.cheongsanghoe.post.api;

import com.likelion.cheongsanghoe.exception.Response;
import com.likelion.cheongsanghoe.exception.status.SuccessStatus;
import com.likelion.cheongsanghoe.global.api.dto.response.PaginationDto;
import com.likelion.cheongsanghoe.portfolio.api.dto.response.PortfolioResponseDto;
import com.likelion.cheongsanghoe.post.api.dto.request.PostUpdateRequestDto;
import com.likelion.cheongsanghoe.post.api.dto.response.*;
import com.likelion.cheongsanghoe.post.application.PostService;
import com.likelion.cheongsanghoe.post.api.dto.request.PostSaveRequestDto;
import com.likelion.cheongsanghoe.post.application.SearchService;
import com.likelion.cheongsanghoe.post.domain.Category;
import com.likelion.cheongsanghoe.post.domain.Post;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "공고 생성", description = "공고를 새로 작성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "공고 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증 필요")
    })
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
    @Operation(summary = "postId로 공고 상세 조회", description = "postId로 공고 상세조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "공고 조회 성공",
            content = @Content(schema = @Schema(implementation = PostInfoResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "공고를 찾을 수 없음")
    })
    @GetMapping("/{postId}")
    public Response<PostInfoResponseDto> PostFindById(@Parameter(description = "공고Id", required = true, example = "1") @PathVariable("postId")Long postId) {
        PostInfoResponseDto postInfoResponseDto = postService.getPostId(postId);
        return Response.success(SuccessStatus.POST_SUCCESS,postInfoResponseDto);
    }
    //공고 전체 조회(요약 정보)
    @Operation(summary = "공고 전체 조회(요약된 공고 정보), 키워드랑 카테고리 쿼리 파라미터로 받는다(검색기능)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "공고 조회 성공")
    })
    @GetMapping
    public Response<PostPageResponseDto> postFindAll(
            @Parameter(description = "공고 제목이나 내용을 입력하면 키워드로 검색", example = "20초 영상")
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            //PageableDefault로 페이지네이션 기본값 적용, pageable 파라미터 추가
            @PageableDefault(size = 10, sort = "createAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Category categoryEnum = Category.from(category);

        Page<PostSummaryResponseDto> postPageDto = searchService.searchPosts(keyword, categoryEnum, pageable);

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
    @Operation(summary = "postId로 공고 수정", description = "postId로 해당 공고 내용 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode ="200", description = "공고가 수정되었습니다."),
            @ApiResponse(responseCode ="400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "403", description = "수정 권한 없음"),
    })
    @PatchMapping("/{postId}")
    public Response<PostInfoResponseDto> postUpdate(@PathVariable("postId")Long postId, @RequestBody PostUpdateRequestDto postUpdateRequestDto) {
        postService.postUpdate(postId, postUpdateRequestDto);
        PostInfoResponseDto postInfoResponseDto = postService.getPostId(postId);
        return Response.success(SuccessStatus.POST_UPDATED,postInfoResponseDto);
    }
    //공고 삭제
    @Operation(summary = "공고 삭제", description = "postId 받아서 해당 공고 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "공고 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "이미 삭제된 공고입니다.")
    })
    @DeleteMapping("/{postId}")
    public Response<String> postDelete(@Parameter(description = "공고 ID", required = true, example = "1")@PathVariable("postId")Long postId) {
        postService.postDelete(postId);
        return Response.success(SuccessStatus.POST_DELETE,null);
    }

}


