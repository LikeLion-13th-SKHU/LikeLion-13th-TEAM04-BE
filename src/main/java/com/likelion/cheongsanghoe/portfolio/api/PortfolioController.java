package com.likelion.cheongsanghoe.portfolio.api;

import com.likelion.cheongsanghoe.portfolio.api.dto.request.PortfolioCreateRequestDto;
import com.likelion.cheongsanghoe.portfolio.api.dto.request.PortfolioUpdateRequestDto;
import com.likelion.cheongsanghoe.portfolio.api.dto.response.PortfolioResponseDto;
import com.likelion.cheongsanghoe.portfolio.application.PortfolioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/portfolios")
@RequiredArgsConstructor
@Tag(name = "Portfolio", description = "포트폴리오 관리 API")
public class PortfolioController {

    private final PortfolioService portfolioService;

    @Operation(summary = "포트폴리오 생성", description = "새로운 포트폴리오를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "포트폴리오 생성 성공",
                    content = @Content(schema = @Schema(implementation = PortfolioResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    @PostMapping
    public ResponseEntity<PortfolioResponseDto> createPortfolio(
            @Parameter(description = "포트폴리오 생성 요청 데이터", required = true)
            @Valid @RequestBody PortfolioCreateRequestDto requestDto,
            Authentication authentication) {
        log.info("Creating portfolio for user: {}", authentication.getName());

        String email = authentication.getName();
        PortfolioResponseDto response = portfolioService.createPortfolio(email, requestDto);
        return ResponseEntity.created(URI.create("/api/portfolios/" + response.getPortfolioId())).body(response);
    }

    @Operation(summary = "포트폴리오 상세 조회", description = "특정 포트폴리오의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "포트폴리오 조회 성공",
                    content = @Content(schema = @Schema(implementation = PortfolioResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "포트폴리오를 찾을 수 없음")
    })
    @GetMapping("/{portfolioId}")
    public ResponseEntity<PortfolioResponseDto> getPortfolio(
            @Parameter(description = "포트폴리오 ID", required = true, example = "1")
            @PathVariable Long portfolioId) {
        log.info("Getting portfolio: {}", portfolioId);

        PortfolioResponseDto response = portfolioService.getPortfolio(portfolioId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "회원별 포트폴리오 목록 조회", description = "특정 회원이 작성한 포트폴리오 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "포트폴리오 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음")
    })
    @GetMapping("/member/{memberId}")
    public ResponseEntity<Page<PortfolioResponseDto>> getMemberPortfolios(
            @Parameter(description = "회원 ID", required = true, example = "1")
            @PathVariable Long memberId,
            @Parameter(description = "페이징 정보 (page, size, sort)",
                    example = "page=0&size=20&sort=createdAt,desc")
            Pageable pageable) {
        log.info("Getting portfolios for member: {}", memberId);

        Page<PortfolioResponseDto> response = portfolioService.getMemberPortfolios(memberId, pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "포트폴리오 검색", description = "키워드를 통해 포트폴리오를 검색합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 성공")
    })
    @GetMapping("/search")
    public ResponseEntity<Page<PortfolioResponseDto>> searchPortfolios(
            @Parameter(description = "검색 키워드",
                    example = "Spring Boot",
                    required = false)
            @RequestParam(required = false) String keyword,
            @Parameter(description = "페이징 정보 (page, size, sort)",
                    example = "page=0&size=20&sort=createdAt,desc")
            Pageable pageable) {
        log.info("Searching portfolios with keyword: {}", keyword);

        Page<PortfolioResponseDto> response = portfolioService.searchPortfolios(keyword, pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "포트폴리오 수정", description = "기존 포트폴리오를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "포트폴리오 수정 성공",
                    content = @Content(schema = @Schema(implementation = PortfolioResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
            @ApiResponse(responseCode = "403", description = "수정 권한 없음"),
            @ApiResponse(responseCode = "404", description = "포트폴리오를 찾을 수 없음")
    })
    @PutMapping("/{portfolioId}")
    public ResponseEntity<PortfolioResponseDto> updatePortfolio(
            @Parameter(description = "포트폴리오 ID", required = true, example = "1")
            @PathVariable Long portfolioId,
            @Parameter(description = "포트폴리오 수정 요청 데이터", required = true)
            @Valid @RequestBody PortfolioUpdateRequestDto requestDto,
            Authentication authentication) {
        log.info("Updating portfolio: {} by user: {}", portfolioId, authentication.getName());

        String email = authentication.getName();
        PortfolioResponseDto response = portfolioService.updatePortfolio(portfolioId, email, requestDto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "포트폴리오 삭제", description = "포트폴리오를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "포트폴리오 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 필요"),
            @ApiResponse(responseCode = "403", description = "삭제 권한 없음"),
            @ApiResponse(responseCode = "404", description = "포트폴리오를 찾을 수 없음")
    })
    @DeleteMapping("/{portfolioId}")
    public ResponseEntity<Map<String, String>> deletePortfolio(
            @Parameter(description = "포트폴리오 ID", required = true, example = "1")
            @PathVariable Long portfolioId,
            Authentication authentication) {
        log.info("Deleting portfolio: {} by user: {}", portfolioId, authentication.getName());

        String email = authentication.getName();
        portfolioService.deletePortfolio(portfolioId, email);

        Map<String, String> response = new HashMap<>();
        response.put("message", "포트폴리오가 성공적으로 삭제되었습니다.");
        return ResponseEntity.ok(response);
    }
}