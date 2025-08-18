package com.likelion.cheongsanghoe.portfolio.api;

import com.likelion.cheongsanghoe.portfolio.api.dto.request.PortfolioCreateRequestDto;
import com.likelion.cheongsanghoe.portfolio.api.dto.request.PortfolioUpdateRequestDto;
import com.likelion.cheongsanghoe.portfolio.api.dto.response.PortfolioResponseDto;
import com.likelion.cheongsanghoe.portfolio.application.PortfolioService;
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
public class PortfolioController {

    private final PortfolioService portfolioService;

    @PostMapping
    public ResponseEntity<PortfolioResponseDto> createPortfolio(
            @Valid @RequestBody PortfolioCreateRequestDto requestDto,
            Authentication authentication) {
        log.info("Creating portfolio for user: {}", authentication.getName());

        String email = authentication.getName();
        PortfolioResponseDto response = portfolioService.createPortfolio(email, requestDto);
        return ResponseEntity.created(URI.create("/api/portfolios/" + response.getPortfolioId())).body(response);
    }

    @GetMapping("/{portfolioId}")
    public ResponseEntity<PortfolioResponseDto> getPortfolio(@PathVariable Long portfolioId) {
        log.info("Getting portfolio: {}", portfolioId);

        PortfolioResponseDto response = portfolioService.getPortfolio(portfolioId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<Page<PortfolioResponseDto>> getMemberPortfolios(
            @PathVariable Long memberId,
            Pageable pageable) {
        log.info("Getting portfolios for member: {}", memberId);

        Page<PortfolioResponseDto> response = portfolioService.getMemberPortfolios(memberId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<PortfolioResponseDto>> searchPortfolios(
            @RequestParam(required = false) String keyword,
            Pageable pageable) {
        log.info("Searching portfolios with keyword: {}", keyword);

        Page<PortfolioResponseDto> response = portfolioService.searchPortfolios(keyword, pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{portfolioId}")
    public ResponseEntity<PortfolioResponseDto> updatePortfolio(
            @PathVariable Long portfolioId,
            @Valid @RequestBody PortfolioUpdateRequestDto requestDto,
            Authentication authentication) {
        log.info("Updating portfolio: {} by user: {}", portfolioId, authentication.getName());

        String email = authentication.getName();
        PortfolioResponseDto response = portfolioService.updatePortfolio(portfolioId, email, requestDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{portfolioId}")
    public ResponseEntity<Map<String, String>> deletePortfolio(
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