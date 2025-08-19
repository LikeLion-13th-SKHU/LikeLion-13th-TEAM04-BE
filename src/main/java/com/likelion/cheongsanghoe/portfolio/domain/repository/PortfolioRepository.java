package com.likelion.cheongsanghoe.portfolio.domain.repository;

import com.likelion.cheongsanghoe.member.domain.Member;
import com.likelion.cheongsanghoe.portfolio.domain.Portfolio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    Page<Portfolio> findByMember(Member member, Pageable pageable);

    Page<Portfolio> findByTitleContaining(String title, Pageable pageable);

    Page<Portfolio> findByTitleContainingOrContentContaining(String title, String content, Pageable pageable);

}