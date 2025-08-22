package com.likelion.cheongsanghoe.portfolio.domain;

import com.likelion.cheongsanghoe.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "portfolios")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "portfolio_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, length = 100)
    private String title;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(length = 255)
    private String projectUrl;

    @Column(length = 255)
    private String thumbnailUrl;

    @Column(length = 100)
    private String category;

    @Column(columnDefinition = "TEXT")
    private String skills;

    @Column(length = 50)
    private String experience;

    @Column(length = 100)
    private String hourlyRate;

    @Embedded
    private AvailableTime availableTime;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public Portfolio(Member member, String title, String content, String projectUrl, String thumbnailUrl,
                     String category, String skills, String experience, String hourlyRate, AvailableTime availableTime) {
        this.member = member;
        this.title = title;
        this.content = content;
        this.projectUrl = projectUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.category = category;
        this.skills = skills;
        this.experience = experience;
        this.hourlyRate = hourlyRate;
        this.availableTime = availableTime;
    }

    public void update(String title, String content, String projectUrl, String thumbnailUrl,
                       String category, String skills, String experience, String hourlyRate, AvailableTime availableTime) {
        this.title = title;
        this.content = content;
        this.projectUrl = projectUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.category = category;
        this.skills = skills;
        this.experience = experience;
        this.hourlyRate = hourlyRate;
        this.availableTime = availableTime;
    }
}