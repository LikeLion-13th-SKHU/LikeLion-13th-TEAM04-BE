package com.likelion.cheongsanghoe.portfolio.application;

import com.likelion.cheongsanghoe.auth.domain.User;
import com.likelion.cheongsanghoe.auth.domain.repository.UserRepository;
import com.likelion.cheongsanghoe.member.domain.Member;
import com.likelion.cheongsanghoe.member.domain.repository.MemberRepository;
import com.likelion.cheongsanghoe.portfolio.api.dto.request.AvailableTimeDto;
import com.likelion.cheongsanghoe.portfolio.api.dto.request.PortfolioCreateRequestDto;
import com.likelion.cheongsanghoe.portfolio.api.dto.request.PortfolioUpdateRequestDto;
import com.likelion.cheongsanghoe.portfolio.api.dto.response.PortfolioResponseDto;
import com.likelion.cheongsanghoe.portfolio.domain.AvailableTime;
import com.likelion.cheongsanghoe.portfolio.domain.Portfolio;
import com.likelion.cheongsanghoe.portfolio.domain.repository.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final MemberRepository memberRepository;
    private final UserRepository userRepository;

    public PortfolioResponseDto createPortfolio(String email, PortfolioCreateRequestDto requestDto) {
        log.info("Creating portfolio for user: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        Member member = memberRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));

        AvailableTime availableTime = convertToAvailableTime(requestDto.getAvailableTime());

        Portfolio portfolio = Portfolio.builder()
                .member(member)
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .projectUrl(requestDto.getProjectUrl())
                .thumbnailUrl(requestDto.getThumbnailUrl())
                .category(requestDto.getCategory())
                .skills(requestDto.getSkills())
                .experience(requestDto.getExperience())
                .hourlyRate(requestDto.getHourlyRate())
                .availableTime(availableTime)
                .build();

        Portfolio savedPortfolio = portfolioRepository.save(portfolio);
        log.info("Portfolio created successfully. ID: {}", savedPortfolio.getId());
        return PortfolioResponseDto.from(savedPortfolio);
    }

    @Transactional(readOnly = true)
    public PortfolioResponseDto getPortfolio(Long portfolioId) {
        log.info("Getting portfolio: {}", portfolioId);

        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new RuntimeException("포트폴리오를 찾을 수 없습니다."));
        return PortfolioResponseDto.from(portfolio);
    }

    @Transactional(readOnly = true)
    public Page<PortfolioResponseDto> getMemberPortfolios(Long memberId, Pageable pageable) {
        log.info("Getting portfolios for member: {}", memberId);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));

        Page<Portfolio> portfolios = portfolioRepository.findByMember(member, pageable);
        return portfolios.map(PortfolioResponseDto::from);
    }

    @Transactional(readOnly = true)
    public Page<PortfolioResponseDto> searchPortfolios(String keyword, String category, String skills, String experience, String hourlyRate, Pageable pageable) {
        log.info("Searching portfolios with keyword: {}, category: {}, skills: {}, experience: {}, hourlyRate: {}", keyword, category, skills, experience, hourlyRate);

        Page<Portfolio> portfolios;

        if ((keyword == null || keyword.trim().isEmpty()) &&
                (category == null || category.trim().isEmpty()) &&
                (skills == null || skills.trim().isEmpty()) &&
                (experience == null || experience.trim().isEmpty()) &&
                (hourlyRate == null || hourlyRate.trim().isEmpty())) {
            // 모든 검색 조건이 없으면 전체 조회
            portfolios = portfolioRepository.findAll(pageable);
        } else {
            // 조건에 따른 검색
            portfolios = portfolioRepository.findBySearchCriteria(keyword, category, skills, experience, hourlyRate, pageable);
        }

        return portfolios.map(PortfolioResponseDto::from);
    }

    public PortfolioResponseDto updatePortfolio(Long portfolioId, String email, PortfolioUpdateRequestDto requestDto) {
        log.info("Updating portfolio: {} by user: {}", portfolioId, email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        Member member = memberRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));

        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new RuntimeException("포트폴리오를 찾을 수 없습니다."));

        if (!portfolio.getMember().getId().equals(member.getId())) {
            throw new RuntimeException("포트폴리오를 수정할 권한이 없습니다.");
        }

        AvailableTime availableTime = convertToAvailableTime(requestDto.getAvailableTime());

        portfolio.update(
                requestDto.getTitle() != null ? requestDto.getTitle() : portfolio.getTitle(),
                requestDto.getContent() != null ? requestDto.getContent() : portfolio.getContent(),
                requestDto.getProjectUrl() != null ? requestDto.getProjectUrl() : portfolio.getProjectUrl(),
                requestDto.getThumbnailUrl() != null ? requestDto.getThumbnailUrl() : portfolio.getThumbnailUrl(),
                requestDto.getCategory() != null ? requestDto.getCategory() : portfolio.getCategory(),
                requestDto.getSkills() != null ? requestDto.getSkills() : portfolio.getSkills(),
                requestDto.getExperience() != null ? requestDto.getExperience() : portfolio.getExperience(),
                requestDto.getHourlyRate() != null ? requestDto.getHourlyRate() : portfolio.getHourlyRate(),
                availableTime != null ? availableTime : portfolio.getAvailableTime()
        );

        log.info("Portfolio updated successfully. ID: {}", portfolioId);
        return PortfolioResponseDto.from(portfolio);
    }

    public void deletePortfolio(Long portfolioId, String email) {
        log.info("Deleting portfolio: {} by user: {}", portfolioId, email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        Member member = memberRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));

        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new RuntimeException("포트폴리오를 찾을 수 없습니다."));

        if (!portfolio.getMember().getId().equals(member.getId())) {
            throw new RuntimeException("포트폴리오를 삭제할 권한이 없습니다.");
        }

        portfolioRepository.delete(portfolio);
        log.info("Portfolio deleted successfully. ID: {}", portfolioId);
    }

    private AvailableTime convertToAvailableTime(AvailableTimeDto dto) {
        if (dto == null) {
            return null;
        }
        return new AvailableTime(dto.getWeekday(), dto.getWeekend(), dto.getEvening(), dto.getFlexible());
    }
}