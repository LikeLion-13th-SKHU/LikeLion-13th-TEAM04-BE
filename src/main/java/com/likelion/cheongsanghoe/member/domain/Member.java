package com.likelion.cheongsanghoe.member.domain;

import com.likelion.cheongsanghoe.auth.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "members")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column(length = 20)
    private String phoneNumber;

    @Column(length = 100)
    private String address;

    @Column(length = 100)
    private String detailAddress;

    @Column(length = 10)
    private String zipCode;

    @Column(length = 500)
    private String bio;

    @Column(length = 255)
    private String profileImageUrl;

    @Column(length = 100)
    private String jobCategory;

    @Column(length = 100)
    private String preferredLocation;

    @Column(length = 500)
    private String skills;

    @Column
    private Integer experienceYears;

    @Column(length = 255)
    private String education;

    @Column
    private Integer salaryExpectation;

    @Column(nullable = false)
    private Integer portfolioCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus status;


    // 🚨 [수정] : User 엔티티와 동일한 방식으로 변경
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // 🚨 [수정] : Builder가 status를 받을 수 있도록 생성자에 추가
    @Builder
    public Member(User user, String nickname, String phoneNumber, String address,
                  String detailAddress, String zipCode, String bio, String profileImageUrl,
                  String jobCategory, String preferredLocation, String skills,
                  Integer experienceYears, String education, Integer salaryExpectation,
                  MemberStatus status) { // status 추가
        this.user = user;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.detailAddress = detailAddress;
        this.zipCode = zipCode;
        this.bio = bio;
        this.profileImageUrl = profileImageUrl;
        this.jobCategory = jobCategory;
        this.preferredLocation = preferredLocation;
        this.skills = skills;
        this.experienceYears = experienceYears;
        this.education = education;
        this.salaryExpectation = salaryExpectation;
        this.status = status; // status 필드 초기화
    }

    // 12개 파라미터를 모두 받는 updateProfile 메소드
    public void updateProfile(String nickname, String phoneNumber, String address, String detailAddress,
                              String zipCode, String bio, String jobCategory, String preferredLocation,
                              String skills, Integer experienceYears, String education,
                              Integer salaryExpectation) {
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.detailAddress = detailAddress;
        this.zipCode = zipCode;
        this.bio = bio;
        this.jobCategory = jobCategory;
        this.preferredLocation = preferredLocation;
        this.skills = skills;
        this.experienceYears = experienceYears;
        this.education = education;
        this.salaryExpectation = salaryExpectation;
    }

    public void updateProfileImage(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public void withdraw() {
        this.status = MemberStatus.WITHDRAWN;
    }

    public boolean isActive() {
        return this.status == MemberStatus.ACTIVE;
    }
}
