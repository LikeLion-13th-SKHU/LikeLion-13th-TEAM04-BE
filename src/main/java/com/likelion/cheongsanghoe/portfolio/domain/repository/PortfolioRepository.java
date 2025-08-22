package com.likelion.cheongsanghoe.portfolio.domain.repository;

import com.likelion.cheongsanghoe.member.domain.Member;
import com.likelion.cheongsanghoe.portfolio.domain.Portfolio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    Page<Portfolio> findByMember(Member member, Pageable pageable);

    Page<Portfolio> findByTitleContaining(String title, Pageable pageable);

    Page<Portfolio> findByTitleContainingOrContentContaining(String title, String content, Pageable pageable);

    @Query("SELECT p FROM Portfolio p WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR p.title LIKE %:keyword% OR p.content LIKE %:keyword%) AND " +
            "(:category IS NULL OR :category = '' OR p.category LIKE %:category%) AND " +
            "(:skills IS NULL OR :skills = '' OR p.skills LIKE %:skills%) AND " +
            "(:experience IS NULL OR :experience = '' OR p.experience = :experience) AND " +
            "(:hourlyRate IS NULL OR :hourlyRate = '' OR p.hourlyRate LIKE %:hourlyRate%) AND " +
            "(:weekday IS NULL OR p.availableTime.weekday = :weekday) AND " +
            "(:weekend IS NULL OR p.availableTime.weekend = :weekend) AND " +
            "(:evening IS NULL OR p.availableTime.evening = :evening) AND " +
            "(:flexible IS NULL OR p.availableTime.flexible = :flexible)")
    Page<Portfolio> findBySearchCriteriaWithAvailableTime(
            @Param("keyword") String keyword,
            @Param("category") String category,
            @Param("skills") String skills,
            @Param("experience") String experience,
            @Param("hourlyRate") String hourlyRate,
            @Param("weekday") Boolean weekday,
            @Param("weekend") Boolean weekend,
            @Param("evening") Boolean evening,
            @Param("flexible") Boolean flexible,
            Pageable pageable);
}